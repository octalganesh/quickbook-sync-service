package com.octal.supa.clients;

import com.octal.supa.dto.rest.CustomerDTO;
import com.octal.supa.dto.rest.CustomerRestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "admin-service",
        configuration = ClientHederFeignConfig.class
)
public interface AdminServiceClient {

    @PostMapping("/quickBooks-web-connector/event")
    ResponseEntity<?> createCustomerEventCallBack(@RequestBody CustomerRestDTO.CallBackEvent callBackEvent);

    @PostMapping("/quickBooks/customer/update")
    ResponseEntity<?> updateCustomer(@RequestBody CustomerRestDTO.CreateQueue createQueue);

    @PostMapping("/quickBooks/customer/add")
    ResponseEntity<?> createCustomer(@RequestBody List<CustomerDTO> customerDTOList);//sync customers to local
}

