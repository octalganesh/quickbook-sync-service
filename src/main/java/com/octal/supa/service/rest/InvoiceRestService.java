package com.octal.supa.service.rest;

import com.octal.supa.dto.rest.CustomerRestDTO;
import com.octal.supa.dto.rest.InvoiceRestDTO;
import com.octal.supa.exceptions.CodeException;

public interface InvoiceRestService {

    String createInvoiceQueue(InvoiceRestDTO.Add add) throws CodeException;

}
