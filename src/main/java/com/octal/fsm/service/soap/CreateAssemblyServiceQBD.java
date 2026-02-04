package com.octal.fsm.service.soap;

import org.springframework.stereotype.Service;

@Service
public interface CreateAssemblyServiceQBD {

    String getSyncAuthToken();

    String syncAssemblyFromQueue();

    void createSyncAssemblyFromQBWC(String xmlPayload) throws Exception;
}
