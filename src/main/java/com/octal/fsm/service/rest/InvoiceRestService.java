package com.octal.fsm.service.rest;

import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.exceptions.CodeException;

public interface InvoiceRestService {

    String createInvoiceQueue(InvoiceRestDTO.Add add) throws CodeException;

}
