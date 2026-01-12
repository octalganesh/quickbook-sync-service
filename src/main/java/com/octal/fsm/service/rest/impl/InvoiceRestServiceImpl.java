package com.octal.fsm.service.rest.impl;

import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.entities.CreateInvoiceQueue;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.repositories.CreateInvoiceQueueRepository;
import com.octal.fsm.service.rest.InvoiceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceRestServiceImpl implements InvoiceRestService {

    @Autowired
    private CreateInvoiceQueueRepository createInvoiceQueueRepository;

    @Override
    public String createInvoiceQueue(InvoiceRestDTO.Add dto) throws CodeException {
        if(dto.getRefId()!=null && !dto.getRefId().isEmpty()) {
            if (createInvoiceQueueRepository.findByRefId(dto.getRefId()).isPresent()) {
                throw new CodeException("Invoice is already queued.", ErrorCode.COMMON);
            }
        }
        CreateInvoiceQueue entity = new CreateInvoiceQueue();
        entity.setRefId(dto.getRefId());
        entity.setInvoiceId(dto.getInvoiceId());
        entity.setListId(dto.getListId());
        entity.setCustomerListId(dto.getCustomerListId());
        entity.setCustomerFullName(dto.getCustomerFullName());
        entity.setSyncStatus(dto.getSyncStatus() != null ? dto.getSyncStatus() : "QUEUE");
        entity.setAmount(dto.getAmount());
        entity.setCreateInvoiceJsonResponse(dto.getCreateInvoiceJsonResponse());
        entity.setCreateInvoiceXmlResponse(dto.getCreateInvoiceXmlResponse());
        entity.setStatusCode(dto.getStatusCode());
        entity.setStatusSeverity(dto.getStatusSeverity());
        entity.setStatusMessage(dto.getStatusMessage());
        entity.setActiveToken(dto.getActiveToken());
        CreateInvoiceQueue saved = createInvoiceQueueRepository.save(entity);
        return saved.getUuid();
    }
}
