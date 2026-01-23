package com.octal.fsm.service.soap.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.dto.soap.InvoiceDTO;
import com.octal.fsm.entities.CreateInvoiceQueue;
import com.octal.fsm.event.InvoiceSyncEvent;
import com.octal.fsm.repositories.CreateInvoiceQueueRepository;
import com.octal.fsm.service.soap.InvoiceService;
import com.octal.fsm.utils.ObjectOrArrayAdapter;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private CreateInvoiceQueueRepository createInvoiceQueueRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void syncInvoicesFromQuickBooks(String xmlPayload) throws Exception {
        try {
            String json = XmlUtil.convertXmlToJson(xmlPayload);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                            new TypeToken<List<InvoiceDTO.InvoiceRet>>() {}.getType(),
                            new ObjectOrArrayAdapter<>(InvoiceDTO.InvoiceRet.class)
                    )
                    .registerTypeAdapter(
                            new TypeToken<List<InvoiceDTO.ReceivePaymentRet>>() {}.getType(),
                            new ObjectOrArrayAdapter<>(InvoiceDTO.ReceivePaymentRet.class)
                    )
                    .registerTypeAdapter(
                            new TypeToken<List<InvoiceDTO.AppliedToTxnRet>>() {}.getType(),
                            new ObjectOrArrayAdapter<>(InvoiceDTO.AppliedToTxnRet.class)
                    )
                    .registerTypeAdapter(
                            new TypeToken<List<InvoiceDTO.LinkedTxn>>() {}.getType(),
                            new ObjectOrArrayAdapter<>(InvoiceDTO.LinkedTxn.class)
                    )
                    .create();

            InvoiceDTO dto = gson.fromJson(json, InvoiceDTO.class);
            if (dto == null ||
                    dto.getQBXMLMsgsRs() == null ||
                    dto.getQBXMLMsgsRs().getInvoiceQueryRs() == null ||
                    dto.getQBXMLMsgsRs().getInvoiceQueryRs().getInvoiceRet() == null) {
                return;
            }

            Map<String, String> invoicePaymentTypeMap = new HashMap<>();
            List<InvoiceDTO.ReceivePaymentRet> payments = dto.getQBXMLMsgsRs().getReceivePaymentQueryRs().getReceivePaymentRet();
            for (InvoiceDTO.ReceivePaymentRet pay : payments) {
                String paymentType = pay.getPaymentMethodRef() != null ? pay.getPaymentMethodRef().getFullName().getValue() : null;
                if (pay.getAppliedToTxnRet() != null) {
                    for (InvoiceDTO.AppliedToTxnRet applied : pay.getAppliedToTxnRet()) {
                        invoicePaymentTypeMap.put(applied.getTxnID().getValue(),paymentType);
                    }
                }
            }

            List<InvoiceDTO.InvoiceRet> invoices = dto.getQBXMLMsgsRs().getInvoiceQueryRs().getInvoiceRet();
            List<InvoiceRestDTO.Add> createInvoiceQueues = new ArrayList<>();
            for (InvoiceDTO.InvoiceRet inv : invoices) {
                String refId = inv.getRefNumber() != null ? inv.getRefNumber().getValue() : null;
                if (refId == null) continue;
                BigDecimal subTotal = inv.getSubtotal() != null ? inv.getSubtotal().getValue() : BigDecimal.ZERO;
                BigDecimal balanceRemaining = inv.getBalanceRemaining() != null ? inv.getBalanceRemaining().getValue() : BigDecimal.ZERO;
                BigDecimal taxAmount = inv.getSalesTaxTotal() != null ? inv.getSalesTaxTotal().getValue() : BigDecimal.ZERO;
                Boolean isPaid = inv.getIsPaid() != null ? inv.getIsPaid().getValue() : Boolean.FALSE;
                BigDecimal totalWithTax = subTotal.add(taxAmount);

                InvoiceRestDTO.Add add = new InvoiceRestDTO.Add();
                add.setRefId(refId);
                if(invoicePaymentTypeMap.containsKey(inv.getTxnID().getValue())){
                    String paymentType = invoicePaymentTypeMap.get(inv.getTxnID().getValue());
                    add.setPaymentType(paymentType); // Cash / Card / Check
                }
                add.setTxnId(inv.getTxnID() != null ? inv.getTxnID().getValue() : null);
                add.setIsPaid(isPaid);
                add.setBalanceDue(balanceRemaining.toPlainString());
                add.setTotalAmountWithTax(totalWithTax.toPlainString());
                createInvoiceQueues.add(add);
            }
            if (!createInvoiceQueues.isEmpty()) {
                syncInvoiceFromQueueScheduler(createInvoiceQueues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncInvoiceFromQueueScheduler(List<InvoiceRestDTO.Add> createInvoiceQueues) {
        eventPublisher.publishEvent(new InvoiceSyncEvent(null, createInvoiceQueues));
    }
}
