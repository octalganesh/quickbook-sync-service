package com.octal.fsm.controller.soap;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.service.soap.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sync/invoice")
public class QuickBookSyncInvoiceController {

    @Autowired
    private InvoiceService invoiceService;
    @GetMapping(value = "/list")
    public ResponseEntity<ApiResponse> items(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping(
            value = "/list",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<String> sync(@RequestBody String xmlPayload) {

        try {
            if (xmlPayload.contains("<serverVersion")) {
                return soapResponse(serverVersionResponse());
            }

            if (xmlPayload.contains("<clientVersion")) {
                return soapResponse(clientVersionResponse());
            }
            if (xmlPayload.contains("<authenticate")) {
                return soapResponse(authenticateResponse());
            }

            if (xmlPayload.contains("<sendRequestXML")) {
                return soapResponse(sendRequestXMLResponse());
            }

            if (xmlPayload.contains("<receiveResponseXML")) {
                if (xmlPayload.contains("InvoiceRet")) {
                    invoiceService.syncInvoicesFromQuickBooks(xmlPayload);
                }
                return soapResponse(receiveResponseXMLResponse());
            }

            if (xmlPayload.contains("<getLastError")) {
                return soapResponse(getLastErrorResponse());
            }

            if (xmlPayload.contains("<closeConnection")) {
                return soapResponse(closeConnectionResponse());
            }

            return soapResponse(errorResponse("Unknown request"));

        } catch (Exception e) {
            return soapResponse(errorResponse(e.getMessage()));
        }
    }

    private ResponseEntity<String> soapResponse(String body) {
        return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(body);
    }

    // ================= SOAP RESPONSES =================
    private String serverVersionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<serverVersionResponse xmlns=\"http://developer.intuit.com/\">" +
                "<serverVersionResult/>" +
                "</serverVersionResponse>" +
                "</soap:Body></soap:Envelope>";
    }

    private String clientVersionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<clientVersionResponse xmlns=\"http://developer.intuit.com/\">" +
                "<clientVersionResult/>" +
                "</clientVersionResponse>" +
                "</soap:Body></soap:Envelope>";
    }

    private String authenticateResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<authenticateResponse xmlns=\"http://developer.intuit.com/\">" +
                "<authenticateResult>" +
                "<string>session123</string>" +
                "<string/>" +
                "</authenticateResult>" +
                "</authenticateResponse>" +
                "</soap:Body></soap:Envelope>";
    }
    private String sendRequestXMLResponse() {
        return "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "<sendRequestXMLResult><![CDATA[" +

                "<?qbxml version=\"17.0\"?>" +
                "<QBXML>" +
                "<QBXMLMsgsRq onError=\"stopOnError\">" +
                "<InvoiceQueryRq requestID=\"1\">" +
                "</InvoiceQueryRq>" +

                // 2️⃣ Receive payment query (THIS gives PaymentMethodRef)
                "<ReceivePaymentQueryRq requestID=\"2\">" +
                "<IncludeLineItems>true</IncludeLineItems>" +
                "</ReceivePaymentQueryRq>" +

                "</QBXMLMsgsRq>" +
                "</QBXML>" +

                "]]></sendRequestXMLResult>" +
                "</sendRequestXMLResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }


    private String receiveResponseXMLResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<receiveResponseXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "<receiveResponseXMLResult>100</receiveResponseXMLResult>" +
                "</receiveResponseXMLResponse>" +
                "</soap:Body></soap:Envelope>";
    }

    private String getLastErrorResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>No Error</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body></soap:Envelope>";
    }

    private String closeConnectionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<closeConnectionResponse xmlns=\"http://developer.intuit.com/\">" +
                "<closeConnectionResult>Done</closeConnectionResult>" +
                "</closeConnectionResponse>" +
                "</soap:Body></soap:Envelope>";
    }

    private String errorResponse(String msg) {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>" + msg + "</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body></soap:Envelope>";
    }
}

