package com.octal.fsm.service.soap;

public interface CreateAccountService {

    void createSyncAccountFromQuickBookWebConnector(String xmlPayload) throws Exception;
}
