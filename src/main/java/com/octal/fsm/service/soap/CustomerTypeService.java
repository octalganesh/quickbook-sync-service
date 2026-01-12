package com.octal.fsm.service.soap;

import org.springframework.stereotype.Service;

@Service
public interface CustomerTypeService {
    void syncCustomerTypeFromQuickBookWebConnector(String xmlPayload) throws Exception;
}