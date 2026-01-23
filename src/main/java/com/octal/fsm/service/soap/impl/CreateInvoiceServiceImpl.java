package com.octal.fsm.service.soap.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.dto.soap.CreateInvoiceResponse;
import com.octal.fsm.entities.CreateCustomerQueue;
import com.octal.fsm.entities.CreateInvoiceQueue;
import com.octal.fsm.event.InvoiceSyncEvent;
import com.octal.fsm.repositories.CreateCustomerQueueRepository;
import com.octal.fsm.repositories.CreateInvoiceQueueRepository;
import com.octal.fsm.service.soap.CreateInvoiceService;
import com.octal.fsm.utils.TextUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateInvoiceServiceImpl implements CreateInvoiceService {

    @Autowired
    private CreateInvoiceQueueRepository createInvoiceQueueRepository;
    @Autowired
    private CreateCustomerQueueRepository createCustomerQueueRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void createSyncInvoiceFromQuickBookWebConnector(String xmlPayload) throws Exception {
        String payloadJson = convertResponseXmlToJson(xmlPayload);
        Gson gson = new Gson();
        CreateInvoiceResponse createInvoiceResponse = gson.fromJson(payloadJson, CreateInvoiceResponse.class);
        if (createInvoiceResponse != null && !TextUtils.isEmpty(createInvoiceResponse.getTicket())) {
            Optional<CreateInvoiceQueue> createInvoiceQueue = createInvoiceQueueRepository.findByUuid(createInvoiceResponse.getTicket());
            if (createInvoiceQueue.isPresent()) {
                createInvoiceQueue.get().setStatusCode(createInvoiceResponse.getQBXMLMsgsRs() != null ?
                        createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null ?
                                !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusCode()) ? createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusCode() : null : null : null);
                createInvoiceQueue.get().setStatusSeverity(createInvoiceResponse.getQBXMLMsgsRs() != null ?
                        createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null ?
                                !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusSeverity()) ? createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusSeverity() : null : null : null);
                createInvoiceQueue.get().setStatusMessage(createInvoiceResponse.getQBXMLMsgsRs() != null ?
                        createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null ?
                                !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusMessage()) ? createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusMessage() : null : null : null);
                createInvoiceQueue.get().setListId(createInvoiceResponse.getQBXMLMsgsRs() != null ?
                        createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null ?
                                createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet() != null ? !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet().getListID()) ? createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet().getListID()
                                        : null : null : null : null);
                createInvoiceQueue.get().setRefId(createInvoiceResponse.getQBXMLMsgsRs() != null ?
                        createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null ?
                                createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet() != null ? !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet().getRefNumber()) ? createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getInvoiceRet().getRefNumber()
                                        : null : null : null : null);
                if (createInvoiceResponse.getQBXMLMsgsRs() != null && createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs() != null && !TextUtils.isEmpty(createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusCode()) && (createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusCode().equalsIgnoreCase("0") || createInvoiceResponse.getQBXMLMsgsRs().getInvoiceAddRs().getStatusCode().equalsIgnoreCase("1"))) {
                    createInvoiceQueue.get().setSyncStatus("SUCCESS");
                    createInvoiceQueue.get().setDeleted(true);
                } else {
                    createInvoiceQueue.get().setSyncStatus("FAILED");
                }
                createInvoiceQueue.get().setCreateInvoiceXmlResponse(xmlPayload);
                createInvoiceQueue.get().setCreateInvoiceJsonResponse(payloadJson);
                createInvoiceQueue.get().setActiveToken(null);
                createInvoiceQueue.get().setIsPaid(false);
                CreateInvoiceQueue savedInvoiceQueue = createInvoiceQueueRepository.save(createInvoiceQueue.get());
                if(savedInvoiceQueue.getSyncStatus().equalsIgnoreCase("SUCCESS")){
                    syncInvoiceFromQueueScheduler(savedInvoiceQueue);
                }
//                customerService.processCustomerIntoDB(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet());
            }
        }
    }

    public void syncInvoiceFromQueueScheduler(CreateInvoiceQueue createInvoiceQueue) {
        if (!TextUtils.isEmpty(createInvoiceQueue.getRefId())) {
            InvoiceRestDTO.Add createQueue = new InvoiceRestDTO.Add();
            createQueue.setInvoiceId(createInvoiceQueue.getInvoiceId());
            createQueue.setRefId(createInvoiceQueue.getRefId());
            createQueue.setIsPaid(false);
            eventPublisher.publishEvent(new InvoiceSyncEvent(createQueue,null));
        }
    }

    @Override
    public String syncInvoiceFromQueue() {
        CreateInvoiceQueue createInvoiceQueue = createInvoiceQueueRepository
                .findNextQueued(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (createInvoiceQueue == null) {
            return emptySoapResponse();
        }

        Optional<CreateCustomerQueue> customerOpt = createCustomerQueueRepository.findByQuickBookCustomerIdOrUuid(createInvoiceQueue.getCustomerListId());
        if (customerOpt.isEmpty()) {
            createInvoiceQueue.setActiveToken(null);
            createInvoiceQueueRepository.save(createInvoiceQueue);
            return emptySoapResponse();
        }
        CreateCustomerQueue customer = customerOpt.get();
        String request = "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <sendRequestXMLResult><![CDATA[" +
                "        <?qbxml version=\"13.0\"?>" +
                "        <QBXML>" +
                "          <QBXMLMsgsRq onError=\"stopOnError\">" +
                "            <InvoiceAddRq>" +
                "               <InvoiceAdd>" +
                "                   <CustomerRef>" +
                "                       <ListID>" + customer.getQuickBookCustomerId() + "</ListID>" +
                "                       <FullName>" + createInvoiceQueue.getCustomerFullName() + "</FullName>" +
                "                   </CustomerRef>" +
                "                   <InvoiceLineAdd>" +
                "                       <ItemRef>" +
                "                           <FullName>UpFront</FullName>" +
                "                       </ItemRef>" +
                "                       <Quantity>1</Quantity>" +
                "                       <Rate>" + createInvoiceQueue.getAmount() + "</Rate>" +
                "                   </InvoiceLineAdd>" +
                "               </InvoiceAdd>" +
                "            </InvoiceAddRq>" +
                "          </QBXMLMsgsRq>" +
                "        </QBXML>" +
                "      ]]></sendRequestXMLResult>" +
                "    </sendRequestXMLResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
        System.out.println(customer.getQuickBookCustomerId());
        createInvoiceQueue.setActiveToken(null);
        createInvoiceQueueRepository.save(createInvoiceQueue);
        return request;
    }

    private String emptySoapResponse() {
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


    @Override
    public String getSyncAuthToken() {
        createInvoiceQueueRepository.resetAllActiveTokens();
        CreateInvoiceQueue createInvoiceQueue = createInvoiceQueueRepository.findNextQueued(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (createInvoiceQueue != null) {
            createInvoiceQueue.setActiveToken(createInvoiceQueue.getUuid());
            createInvoiceQueueRepository.save(createInvoiceQueue);
            return authenticateResponse(createInvoiceQueue.getUuid());
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
