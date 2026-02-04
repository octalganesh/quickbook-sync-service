package com.octal.fsm.controller.soap;

import com.octal.fsm.service.rest.CreateAssemblyService;
import com.octal.fsm.service.soap.CreateAssemblyServiceQBD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync/assembly")
public class QuickBookCreateAssemblyController {

    @Autowired
    private CreateAssemblyServiceQBD createAssemblyServiceQBD;

    @PostMapping(
            value = "/create",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<String> syncAssembly(@RequestBody String xmlPayload) {

        try {
            if (xmlPayload.contains("<authenticate")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createAssemblyServiceQBD.getSyncAuthToken());

            } else if (xmlPayload.contains("<sendRequestXML")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(createAssemblyServiceQBD.syncAssemblyFromQueue());

            } else if (xmlPayload.contains("<receiveResponseXML")) {
                createAssemblyServiceQBD.createSyncAssemblyFromQBWC(xmlPayload);
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(receiveResponseXMLResponse());

            } else if (xmlPayload.contains("<getLastError")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(getLastErrorResponse());

            } else if (xmlPayload.contains("<closeConnection")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(closeConnectionResponse());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(errorUnknownMethod());

        } catch (Exception e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_XML)
                    .body(errorSOAP(e.getMessage()));
        }
    }

    private String receiveResponseXMLResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<receiveResponseXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "<receiveResponseXMLResult>100</receiveResponseXMLResult>" +
                "</receiveResponseXMLResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    private String getLastErrorResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>No error</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    private String closeConnectionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<closeConnectionResponse xmlns=\"http://developer.intuit.com/\">" +
                "<closeConnectionResult>Assembly sync completed.</closeConnectionResult>" +
                "</closeConnectionResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    private String errorUnknownMethod() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>Unknown method</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    private String errorSOAP(String msg) {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>ERROR: " + msg + "</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }
}
