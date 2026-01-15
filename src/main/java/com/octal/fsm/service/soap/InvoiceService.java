package com.octal.fsm.service.soap;

public interface InvoiceService {

    void syncInvoicesFromQuickBooks(String xmlPayload) throws Exception;
}
