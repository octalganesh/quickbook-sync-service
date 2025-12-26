package com.octal.supa.service.rest;

import com.octal.supa.dto.rest.CustomerRestDTO;
import com.octal.supa.exceptions.CodeException;

public interface CustomerRestService {
    String createCustomerQueue(CustomerRestDTO.CreateQueue createQueue) throws CodeException;
}
