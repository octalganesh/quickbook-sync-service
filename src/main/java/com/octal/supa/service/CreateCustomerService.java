package com.octal.supa.service;

import org.springframework.stereotype.Service;

@Service
public interface CreateCustomerService {
    void createSyncCustomerFromQuickBookWebConnector(String xmlPayload) throws Exception;
    String syncCustomerFromQueue();
    String getSyncAuthToken();
}
