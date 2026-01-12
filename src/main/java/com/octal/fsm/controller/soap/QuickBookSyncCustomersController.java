package com.octal.fsm.controller.soap;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.service.soap.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/sync/customer")
public class QuickBookSyncCustomersController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/list")
    public ResponseEntity<ApiResponse> items(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("Sync Customer List!", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Sync Customer List!", null, "200", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping(
            value = "/list",
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE},
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_XML_VALUE}
    )
    public ResponseEntity<String> syncItems(@RequestBody String xmlPayload, HttpServletRequest request) {
        try {
            if (xmlPayload.contains("<authenticate")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(authenticateResponse());
            } else if (xmlPayload.contains("<sendRequestXML")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(sendRequestXMLResponse());
            } else if (xmlPayload.contains("<receiveResponseXML")) {
                try {
                    customerService.syncCustomersFromQuickBookWebConnector(xmlPayload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    // 1️⃣ authenticate
    private String authenticateResponse() {
        String token = "session_sync_customer_" + UUID.randomUUID();
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <authenticateResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <authenticateResult>" +
                "        <string>" + token + "</string>" +
                "        <string/>" +
                "      </authenticateResult>" +
                "    </authenticateResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }


    private String sendRequestXMLResponse() {
        String request = "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <sendRequestXMLResult><![CDATA[" +
                "        <?qbxml version=\"13.0\"?>" +
                "        <QBXML>" +
                "          <QBXMLMsgsRq onError=\"stopOnError\">" +
                "            <CustomerQueryRq>" +
                "            </CustomerQueryRq>" +
                "          </QBXMLMsgsRq>" +
                "        </QBXML>" +
                "      ]]></sendRequestXMLResult>" +
                "    </sendRequestXMLResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
        return request;
    }

    // 3️⃣ receiveResponseXML
    private String receiveResponseXMLResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <receiveResponseXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <receiveResponseXMLResult>100</receiveResponseXMLResult>" +
                "    </receiveResponseXMLResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

    // 4️⃣ getLastError
    private String getLastErrorResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <getLastErrorResult>No error</getLastErrorResult>" +
                "    </getLastErrorResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

    // 5️⃣ closeConnection
    private String closeConnectionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <closeConnectionResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <closeConnectionResult>Sync completed successfully.</closeConnectionResult>" +
                "    </closeConnectionResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

    // Unknown method fallback
    private String errorUnknownMethod() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <getLastErrorResult>Unknown method</getLastErrorResult>" +
                "    </getLastErrorResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

    // Error wrapper
    private String errorSOAP(String msg) {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <getLastErrorResult>ERROR: " + msg + "</getLastErrorResult>" +
                "    </getLastErrorResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

}
