package com.octal.supa.clients;

import com.octal.supa.dto.rest.CustomerRestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "admin-service",
        configuration = ClientHederFeignConfig.class
)
public interface AdminServiceClient {

    @PostMapping("/quickBooks-web-connector/event")
    ResponseEntity<?> createCustomerEventCallBack(@RequestBody CustomerRestDTO.CallBackEvent callBackEvent);
}

