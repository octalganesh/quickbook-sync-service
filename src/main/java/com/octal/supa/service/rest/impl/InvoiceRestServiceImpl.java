package com.octal.supa.service.rest.impl;

import com.octal.supa.dto.rest.InvoiceRestDTO;
import com.octal.supa.entities.CreateInvoiceQueue;
import com.octal.supa.exceptions.CodeException;
import com.octal.supa.exceptions.ErrorCode;
import com.octal.supa.repositories.CreateInvoiceQueueRepository;
import com.octal.supa.service.rest.InvoiceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceRestServiceImpl implements InvoiceRestService {

    @Autowired
    private CreateInvoiceQueueRepository createInvoiceQueueRepository;

    @Override
    public String createInvoiceQueue(InvoiceRestDTO.Add dto) throws CodeException {
        if (createInvoiceQueueRepository.findByRefId(dto.getRefId()).isPresent()) {
            throw new CodeException("Invoice is already queued.", ErrorCode.COMMON);
        }
        CreateInvoiceQueue entity = new CreateInvoiceQueue();
        entity.setRefId(dto.getRefId());
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
