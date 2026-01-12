package com.octal.fsm.controller.rest;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.service.rest.InvoiceRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rest/invoice")
public class InvoiceRestController {

    @Autowired
    private InvoiceRestService invoiceRestService;

    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse> createInvoiceQueue(@RequestBody InvoiceRestDTO.Add createQueue, HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("Invoice Queued Successfully!", invoiceRestService.createInvoiceQueue(createQueue), "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
        }
    }
}
