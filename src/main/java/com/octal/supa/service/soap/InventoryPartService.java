package com.octal.supa.service.soap;

import io.netty.handler.codec.CodecException;
import org.springframework.stereotype.Service;

@Service
public interface InventoryPartService {
    void syncItemFromQuickBookWebConnector(String xmlPayload) throws Exception;

    void syncInventoryToJobService() throws Exception;
}
