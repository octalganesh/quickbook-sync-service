package com.octal.supa.service.soap;

import com.octal.supa.dto.rest.InventoryRequestDTO;
import io.netty.handler.codec.CodecException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InventoryPartService {
    void syncItemFromQuickBookWebConnector(String xmlPayload) throws Exception;

    void syncInventoryToJobService() throws Exception;

    void updateInventoryQuantities(List<InventoryRequestDTO.Add> add) throws CodecException;
}
