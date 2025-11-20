package com.octal.supa.controller;

import com.octal.supa.dto.ApiResponse;
import com.octal.supa.service.InventoryPartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/sync/invoice")
public class QuickBookCreateInvoiceController {

    @Autowired
    private InventoryPartService inventoryPartService;

    @GetMapping(value = "/create")
    public ResponseEntity<ApiResponse> items(HttpServletRequest request) {
        try {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("permissions assigned successfully!", null, "200", HttpStatus.OK), HttpStatus.OK);
        }
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
                        .body(authenticateResponse());
            } else if (xmlPayload.contains("<sendRequestXML")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
//                        .body(sendRequestXMLResponse("Tarun Singh", "Doe Enterprises", "9999999999", "tarun@example.com", "123 Street", "Mumbai", "MH", "400001", "India"));
                        .body(sendRequestXMLResponse());
            } else if (xmlPayload.contains("<receiveResponseXML")) {
                System.out.println("==== receiveResponseXML() ====");
                System.out.println(xmlPayload);  // THIS contains the actual QB error
                System.out.println("================================");
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_XML)
                        .body(receiveResponseXMLResponse());
            } else if (xmlPayload.contains("<getLastError")) {
                System.out.println("==== getLastError() ====");
                System.out.println(xmlPayload);  // THIS contains the actual QB error
                System.out.println("================================");
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
        String ticket = "ticket_" + UUID.randomUUID();
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <authenticateResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <authenticateResult>" +
                "        <string>" + ticket + "</string>" +
                "        <string/>" +
                "      </authenticateResult>" +
                "    </authenticateResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }


    // 2️⃣ sendRequestXML
//    private String sendRequestXMLResponse() {
//        return "<?xml version=\"1.0\"?>" +
//                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//                "  <soap:Body>" +
//                "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
//                "      <sendRequestXMLResult><![CDATA[" +
//                "        <?qbxml version=\"13.0\"?>" +
//                "        <QBXML>" +
//                "          <QBXMLMsgsRq onError=\"stopOnError\">" +
//                "            <ItemInventoryQueryRq>" +
//                "              <ActiveStatus>All</ActiveStatus>" +
//                "            </ItemInventoryQueryRq>" +
//                "          </QBXMLMsgsRq>" +
//                "        </QBXML>" +
//                "      ]]></sendRequestXMLResult>" +
//                "    </sendRequestXMLResponse>" +
//                "  </soap:Body>" +
//                "</soap:Envelope>";
//    }

    private String sendRequestXMLResponse(
            String customerName,
            String companyName,
            String phone,
            String email,
            String street,
            String city,
            String state,
            String postalCode,
            String country
    ) {

        String qbxml =
                "<?xml version=\"1.0\"?>\n" +
                        "<?qbxml version=\"13.0\"?>\n" +
                        "<QBXML>\n" +
                        "  <QBXMLMsgsRq onError=\"stopOnError\">\n" +
                        "    <CustomerAddRq>\n" +
                        "      <CustomerAdd>\n" +
                        "        <Name>" + customerName + "</Name>\n" +
                        (companyName != null && !companyName.isEmpty()
                                ? "        <CompanyName>" + companyName + "</CompanyName>\n"
                                : "") +
                        (phone != null && !phone.isEmpty()
                                ? "        <Phone>" + phone + "</Phone>\n"
                                : "") +
                        (email != null && !email.isEmpty()
                                ? "        <Email>" + email + "</Email>\n"
                                : "") +
                        "        <BillAddress>\n" +
                        "          <Addr1>" + street + "</Addr1>\n" +
                        "          <City>" + city + "</City>\n" +
                        "          <State>" + state + "</State>\n" +
                        "          <PostalCode>" + postalCode + "</PostalCode>\n" +
                        "          <Country>" + country + "</Country>\n" +
                        "        </BillAddress>\n" +
                        "      </CustomerAdd>\n" +
                        "    </CustomerAddRq>\n" +
                        "  </QBXMLMsgsRq>\n" +
                        "</QBXML>";

        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <sendRequestXMLResult><![CDATA[\n" + qbxml + "]]></sendRequestXMLResult>" +
                "    </sendRequestXMLResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

//    private String sendRequestXMLResponse() {
//        String request = "<?xml version=\"1.0\"?>" +
//                "<?qbxml version=\"13.0\"?>" +
//                "<QBXML>" +
//                "<QBXMLMsgsRq onError=\"stopOnError\">" +
//                    "<CustomerAddRq>" +
//                        "<CustomerAdd>" +
//                            "<Name>Hardik</Name>" +
//                        "</CustomerAdd>" +
//                    "</CustomerAddRq>" +
//                "</QBXMLMsgsRq>" +
//                "</QBXML>";
//        return request;
//    }

    private String sendRequestXMLResponse() {
        String request =  "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <sendRequestXMLResult><![CDATA[" +
                "        <?qbxml version=\"13.0\"?>" +
                "        <QBXML>" +
                "          <QBXMLMsgsRq onError=\"stopOnError\">" +
                "            <CustomerAddRq>" +
                "               <CustomerAdd>" +
                "                   <Name>Tarun</Name>" +
                "               </CustomerAdd>" +
                "            </CustomerAddRq>" +
                "          </QBXMLMsgsRq>" +
                "        </QBXML>" +
                "      ]]></sendRequestXMLResult>" +
                "    </sendRequestXMLResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
        System.out.println(request);
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
                "      <closeConnectionResult>Customer Create successfully.</closeConnectionResult>" +
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
