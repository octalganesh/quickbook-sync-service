package com.octal.fsm.controller.soap;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.service.soap.CreateInvoiceService;
import com.octal.fsm.utils.SOAPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sync/invoice")
public class QuickBookCreateInvoiceController {


    @Autowired
    private CreateInvoiceService createInvoiceService;

    @GetMapping(value = "/create")
    public ResponseEntity<ApiResponse> createInvoice() {
        return new ResponseEntity<>(new ApiResponse("Create Invoice Heath Check Success!", null, "200", HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(
            value = "/create",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<String> syncItems(@RequestBody String xmlPayload, HttpServletRequest request) {
        try {
            if (xmlPayload.contains("<authenticate")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createInvoiceService.getSyncAuthToken());
            } else if (xmlPayload.contains("<sendRequestXML")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createInvoiceService.syncInvoiceFromQueue());
            } else if (xmlPayload.contains("<receiveResponseXML")) {
                try {
                    createInvoiceService.createSyncInvoiceFromQuickBookWebConnector(xmlPayload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.receiveResponseXMLResponse());
            } else if (xmlPayload.contains("<getLastError")) {
                System.out.println("==== getLastError() ====");
                System.out.println(xmlPayload);  // THIS contains the actual QB error
                System.out.println("================================");
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.getLastErrorResponse());

            } else if (xmlPayload.contains("<closeConnection")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.closeConnectionResponse());
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(SOAPUtil.errorUnknownMethod());

        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(SOAPUtil.errorSOAP(e.getMessage()));
        }
    }
}
