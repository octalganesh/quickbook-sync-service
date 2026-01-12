package com.octal.fsm.service.rest;

import com.octal.fsm.dto.rest.CustomerRestDTO;
import com.octal.fsm.exceptions.CodeException;

public interface CustomerRestService {
    String createCustomerQueue(CustomerRestDTO.CreateQueue createQueue) throws CodeException;
}
