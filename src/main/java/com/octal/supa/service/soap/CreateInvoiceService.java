package com.octal.supa.service.soap;

import org.springframework.stereotype.Service;

@Service
public interface CreateInvoiceService {
    void createSyncInvoiceFromQuickBookWebConnector(String xmlPayload) throws Exception;
    String syncInvoiceFromQueue();
    String getSyncAuthToken();
}
