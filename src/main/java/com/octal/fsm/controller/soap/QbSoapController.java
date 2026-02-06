package com.octal.fsm.controller.soap;

import com.octal.fsm.dto.ApiResponse;
import com.octal.fsm.service.soap.InventoryPartService;
import com.octal.fsm.utils.SOAPUtil;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sync")
public class QbSoapController {

    @Autowired
    private InventoryPartService inventoryPartService;
    @GetMapping(value = "/items")
    public ResponseEntity<ApiResponse> items(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping(
            value = "/items",
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
                    inventoryPartService.syncItemFromQuickBookWebConnector(xmlPayload);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(SOAPUtil.receiveResponseXMLResponse());
            } else if (xmlPayload.contains("<getLastError")) {
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

    // 1️⃣ authenticate
    private String authenticateResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <authenticateResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <authenticateResult>" +
                "        <string>session_token_123</string>" +
                "        <string/>" +
                "      </authenticateResult>" +
                "    </authenticateResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }


    private String sendRequestXMLResponse() {
        String innerQueries = buildItemQueriesRq();
        String request = SOAPUtil.buildSoapQbxmlEnvelope("13.0", innerQueries);
        System.out.println(request);
        return request;
    }

    private String buildItemQueriesRq() {
        return
                "<ItemServiceQueryRq><ActiveStatus>All</ActiveStatus></ItemServiceQueryRq>" +
                        "<ItemNonInventoryQueryRq><ActiveStatus>All</ActiveStatus></ItemNonInventoryQueryRq>" +
                        "<ItemInventoryQueryRq><ActiveStatus>All</ActiveStatus></ItemInventoryQueryRq>" +
                        "<ItemOtherChargeQueryRq><ActiveStatus>All</ActiveStatus></ItemOtherChargeQueryRq>" +
                        "<ItemDiscountQueryRq><ActiveStatus>All</ActiveStatus></ItemDiscountQueryRq>" +
                        "<ItemSalesTaxQueryRq><ActiveStatus>All</ActiveStatus></ItemSalesTaxQueryRq>" +
                        "<ItemInventoryAssemblyQueryRq><ActiveStatus>All</ActiveStatus></ItemInventoryAssemblyQueryRq>";
    }
}
