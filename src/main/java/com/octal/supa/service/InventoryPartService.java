package com.octal.supa.service;

import org.springframework.stereotype.Service;

@Service
public interface InventoryPartService {
    void syncItemFromQuickBookWebConnector(String xmlPayload) throws Exception;
}
