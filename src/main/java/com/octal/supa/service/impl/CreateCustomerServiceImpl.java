package com.octal.supa.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.octal.supa.dto.CreateCustomerResponse;
import com.octal.supa.entities.CreateCustomerQueue;
import com.octal.supa.repositories.CreateCustomerQueueRepository;
import com.octal.supa.service.CreateCustomerService;
import com.octal.supa.utils.TextUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateCustomerServiceImpl implements CreateCustomerService {

    @Autowired
    private CreateCustomerQueueRepository createCustomerQueueRepository;


    @Override
    public void createSyncCustomerFromQuickBookWebConnector(String xmlPayload) throws Exception {
        String payloadJson = convertResponseXmlToJson(xmlPayload);
        Gson gson = new Gson();
        CreateCustomerResponse createCustomerResponse = gson.fromJson(payloadJson, CreateCustomerResponse.class);
        if (createCustomerResponse != null && !TextUtils.isEmpty(createCustomerResponse.getTicket())) {
            Optional<CreateCustomerQueue> createCustomerQueue = createCustomerQueueRepository.findByUuid(createCustomerResponse.getTicket());
            if (createCustomerQueue.isPresent()) {
                createCustomerQueue.get().setStatusCode(createCustomerResponse.getQBXMLMsgsRs() != null ?
                        createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null ?
                                !TextUtils.isEmpty(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusCode()) ? createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusCode() : null : null : null);
                createCustomerQueue.get().setStatusSeverity(createCustomerResponse.getQBXMLMsgsRs() != null ?
                        createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null ?
                                !TextUtils.isEmpty(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusSeverity()) ? createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusSeverity() : null : null : null);
                createCustomerQueue.get().setStatusMessage(createCustomerResponse.getQBXMLMsgsRs() != null ?
                        createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null ?
                                !TextUtils.isEmpty(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusMessage()) ? createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusMessage() : null : null : null);
                createCustomerQueue.get().setQuickBookCustomerId(createCustomerResponse.getQBXMLMsgsRs() != null ?
                        createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null ?
                                createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet() != null ? !TextUtils.isEmpty(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet().getListID()) ? createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet().getListID()
                                        : null : null : null : null);
                if (createCustomerResponse.getQBXMLMsgsRs() != null && createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null && !TextUtils.isEmpty(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusCode()) && (createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusCode().equalsIgnoreCase("0") || createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getStatusCode().equalsIgnoreCase("1"))) {
                    createCustomerQueue.get().setSyncStatus("SUCCESS");
                    createCustomerQueue.get().setDeleted(true);
                } else {
                    createCustomerQueue.get().setSyncStatus("FAILED");
                }
                createCustomerQueue.get().setCreateCustomerXmlResponse(xmlPayload);
                createCustomerQueue.get().setCreateCustomerJsonResponse(payloadJson);
                createCustomerQueue.get().setActiveToken(null);
                createCustomerQueueRepository.save(createCustomerQueue.get());
            }
        }
        System.out.println(createCustomerResponse);
    }


    @Override
    public String syncCustomerFromQueue() {
        CreateCustomerQueue createCustomerQueue = createCustomerQueueRepository
                .findActiveTokenRecord(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (createCustomerQueue != null) {
            String request = "<?xml version=\"1.0\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                    "      <sendRequestXMLResult><![CDATA[" +
                    "        <?qbxml version=\"13.0\"?>" +
                    "        <QBXML>" +
                    "          <QBXMLMsgsRq onError=\"stopOnError\">" +
                    "            <CustomerAddRq>" +
                    "               <CustomerAdd>" +
                    "                   <Name>" + createCustomerQueue.getFullName() + createCustomerQueue.getCustomerId() + "</Name>" +
                    "                   <FirstName>" + createCustomerQueue.getFullName() + "</FirstName>" +
                    "               </CustomerAdd>" +
                    "            </CustomerAddRq>" +
                    "          </QBXMLMsgsRq>" +
                    "        </QBXML>" +
                    "      ]]></sendRequestXMLResult>" +
                    "    </sendRequestXMLResponse>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";
            System.out.println(request);
            createCustomerQueue.setActiveToken(null);
            createCustomerQueueRepository.save(createCustomerQueue);
            return request;
        } else {
            return "<?xml version=\"1.0\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                    "      <sendRequestXMLResult>" +
                    "      </sendRequestXMLResult>" +
                    "    </sendRequestXMLResponse>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";
        }
    }

    @Override
    public String getSyncAuthToken() {
        createCustomerQueueRepository.resetAllActiveTokens();
        CreateCustomerQueue createCustomerQueue = createCustomerQueueRepository.findOneQuery(PageRequest.of(0, 1, Sort.by("createdAt").descending()))
                .stream()
                .findFirst()
                .orElse(null);
        if (createCustomerQueue != null) {
            createCustomerQueue.setActiveToken(createCustomerQueue.getUuid());
            createCustomerQueueRepository.save(createCustomerQueue);
            return authenticateResponse(createCustomerQueue.getUuid());
        } else {
            return authenticateResponse("");
        }
    }

    // 1️⃣ authenticate
    private String authenticateResponse(String token) {
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

    public String convertResponseXmlToJson(String soapXml) {
        try {
            String ticket = extractTagValue(soapXml, "ticket");
            // Step 1: Extract <response>...</response>
            String innerEscapedXml = extractTagValue(soapXml, "response");

            // Step 2: Unescape XML entities (&lt; → <)
            String innerXml = StringEscapeUtils.unescapeXml(innerEscapedXml);

            // Step 3: Convert XML → JSON
            XmlMapper xmlMapper = new XmlMapper();
            JsonNode node = xmlMapper.readTree(innerXml.getBytes());
            ObjectMapper jsonMapper = new ObjectMapper();
//            return jsonMapper.writerWithDefaultPrettyPrinter()
//                    .writeValueAsString(node);

            // Step 5: Build final JSON
            ObjectNode finalJson = jsonMapper.createObjectNode();
            finalJson.put("ticket", ticket);
            finalJson.set("QBXMLMsgsRs", node.get("QBXMLMsgsRs"));

            return jsonMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(finalJson);

        } catch (Exception ex) {
            throw new RuntimeException("Error converting XML to JSON", ex);
        }
    }

    private String extractTagValue(String xml, String tag) {
        String start = "<" + tag + ">";
        String end = "</" + tag + ">";

        int s = xml.indexOf(start);
        int e = xml.indexOf(end);

        if (s == -1 || e == -1) {
            throw new RuntimeException("Tag <" + tag + "> not found");
        }

        return xml.substring(s + start.length(), e).trim();
    }
}
