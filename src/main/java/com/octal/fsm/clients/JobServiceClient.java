package com.octal.fsm.clients;

import com.octal.fsm.dto.rest.InventoryRequestDTO;
import com.octal.fsm.dto.rest.InvoiceRestDTO;
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

    @PostMapping("/jobs/invoice/update-details")
    ResponseEntity<?> updateInvoiceDetails(@RequestBody InvoiceRestDTO.Add add);

    @PostMapping("/jobs/invoice/update-details-list")
    ResponseEntity<?> updateInvoiceDetailsList(@RequestBody List<InvoiceRestDTO.Add> addList);
}
