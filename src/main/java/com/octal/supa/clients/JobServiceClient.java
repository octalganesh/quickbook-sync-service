package com.octal.supa.clients;

import com.octal.supa.dto.ApiResponse;
import com.octal.supa.dto.rest.CustomerRestDTO;
import com.octal.supa.dto.rest.InventoryRequestDTO;
import com.octal.supa.dto.soap.InventoryPartDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(
        name = "job-service",
        configuration = ClientHederFeignConfig.class
)
public interface JobServiceClient {

    @PostMapping("/inventory/add")
    ResponseEntity<?> saveInventory(@RequestBody List<InventoryRequestDTO.Add> addList, @RequestHeader("tenantId") Long tenantId,
                                    @RequestHeader("superAdmin") boolean superAdmin);
}
