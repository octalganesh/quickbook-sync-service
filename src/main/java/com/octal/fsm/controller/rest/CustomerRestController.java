package com.octal.fsm.controller.rest;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.dto.rest.CustomerRestDTO;
import com.octal.fsm.service.rest.CustomerRestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rest/customer")
public class CustomerRestController {

    private final CustomerRestService customerRestService;

    public CustomerRestController(CustomerRestService customerRestService) {
        this.customerRestService = customerRestService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse> createCustomerQueue(@RequestBody CustomerRestDTO.CreateQueue createQueue, HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("Customer Queued Successfully!", customerRestService.createCustomerQueue(createQueue), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
        }
    }
}
