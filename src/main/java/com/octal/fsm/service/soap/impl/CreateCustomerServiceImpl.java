package com.octal.fsm.service.soap.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.octal.fsm.clients.AdminServiceClient;
import com.octal.fsm.dto.rest.CustomerRestDTO;
import com.octal.fsm.dto.soap.CreateCustomerResponse;
import com.octal.fsm.entities.CreateCustomerQueue;
import com.octal.fsm.event.CustomerSyncEvent;
import com.octal.fsm.repositories.CreateCustomerQueueRepository;
import com.octal.fsm.service.soap.CreateCustomerService;
import com.octal.fsm.service.soap.CustomerService;
import com.octal.fsm.utils.TextUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateCustomerServiceImpl implements CreateCustomerService {

    @Autowired
    private CreateCustomerQueueRepository createCustomerQueueRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminServiceClient adminServiceClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


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
                if (createCustomerResponse.getQBXMLMsgsRs() != null && createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs() != null && createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet() != null) {
                    if (createCustomerQueue.get().getSyncStatus().equalsIgnoreCase("SUCCESS")) {
                        CreateCustomerResponse.QBXMLMsgsRs.CustomerAddRs.CustomerRet customerAddRs= createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet();
                        if (!TextUtils.isEmpty(customerAddRs.getListID())) {
                            CustomerRestDTO.CreateQueue createQueue = new CustomerRestDTO.CreateQueue();
                            createQueue.setListId(customerAddRs.getListID());
                            createQueue.setCustomerUuid(createCustomerQueue.get().getCustomerUuid());
                            createQueue.setFullName(customerAddRs.getFullName());
                            syncCustomerFromQueueScheduler(createQueue);
                        }
                    }
                    customerService.processCustomerIntoDB(createCustomerResponse.getQBXMLMsgsRs().getCustomerAddRs().getCustomerRet());
                }
                //processCreateCustomerWebBook(createCustomerQueue.get().getCustomerUuid(), createCustomerQueue.get().getQuickBookCustomerId());
            }
            System.out.println(createCustomerResponse);
        }

    }

    public void syncCustomerFromQueueScheduler(CustomerRestDTO.CreateQueue createQueue) {
        eventPublisher.publishEvent(new CustomerSyncEvent(createQueue));
    }

    @Override
    public String syncCustomerFromQueue() {
        CreateCustomerQueue createCustomerQueue = createCustomerQueueRepository
                .findActiveTokenRecord(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (createCustomerQueue != null) {
            String conditionalElements = "";

            // Salutation (optional)
//            if (createCustomerQueue.getGender() != null && !createCustomerQueue.getGender().isEmpty()) {
//                conditionalElements += "<Salutation>"
//                        + (createCustomerQueue.getGender().equalsIgnoreCase("MALE") ? "Mr"
//                        : createCustomerQueue.getGender().equalsIgnoreCase("FEMALE") ? "Ms." : "")
//                        + "</Salutation>";
//            }

            // Email (optional)
            if (!TextUtils.isEmpty(createCustomerQueue.getEmail())) {
                conditionalElements += "<Email>" + createCustomerQueue.getEmail() + "</Email>";
            }

            // Cc (optional)
            if (!TextUtils.isEmpty(createCustomerQueue.getSecondaryEmail())) {
                conditionalElements += "<Cc>" + createCustomerQueue.getSecondaryEmail() + "</Cc>";
            }

//            // Contact (optional)
            if (!TextUtils.isEmpty(createCustomerQueue.getMobile())) {
                conditionalElements += "<Contact>" + createCustomerQueue.getMobile() + "</Contact>";
            }

            // Phone (optional)
//            if (!TextUtils.isEmpty(createCustomerQueue.getAlterNativeMobile1())) {
//                conditionalElements += "<Phone>" + createCustomerQueue.getAlterNativeMobile1() + "</Phone>";
//            }
//
            // AltPhone (optional)
//            if (!TextUtils.isEmpty(createCustomerQueue.getAlterNativeMobile2())) {
//                conditionalElements += "<AltPhone>" + createCustomerQueue.getAlterNativeMobile2() + "</AltPhone>";
//            }

            // CustomerTypeRef (optional)
            if ((createCustomerQueue.getCustomerTypeId() != null && !createCustomerQueue.getCustomerTypeId().isEmpty()) ||
                    (createCustomerQueue.getCustomerTypeName() != null && !createCustomerQueue.getCustomerTypeName().isEmpty())) {
                conditionalElements += "<CustomerTypeRef>";

                if (createCustomerQueue.getCustomerTypeId() != null && !createCustomerQueue.getCustomerTypeId().isEmpty()) {
                    conditionalElements += "<ListID>" + createCustomerQueue.getCustomerTypeId() + "</ListID>";
                }

                if (createCustomerQueue.getCustomerTypeName() != null && !createCustomerQueue.getCustomerTypeName().isEmpty()) {
                    conditionalElements += "<FullName>" + createCustomerQueue.getCustomerTypeName() + "</FullName>";
                }

                conditionalElements += "</CustomerTypeRef>";
            }

            String request = "<?xml version=\"1.0\"?>" +
                    "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "  <soap:Body>" +
                    "    <sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                    "      <sendRequestXMLResult><![CDATA[" +
                    "<?qbxml version=\"13.0\"?>" +
                    "        <QBXML>" +
                    "          <QBXMLMsgsRq onError=\"stopOnError\">" +
                    "            <CustomerAddRq>" +
                    "               <CustomerAdd>" +
                    "                   <Name>" + createCustomerQueue.getFullName() + "</Name>" +
                    "                   <IsActive>" + createCustomerQueue.getActive() + "</IsActive>" +
                    "                   <FirstName>" + createCustomerQueue.getFullName() + "</FirstName>" +
                    "    " + conditionalElements +
                    "               </CustomerAdd>" +
                    "            </CustomerAddRq>" +
                    "          </QBXMLMsgsRq>" +
                    "        </QBXML>" +
                    "]]></sendRequestXMLResult>" +
                    "    </sendRequestXMLResponse>" +
                    "  </soap:Body>" +
                    "</soap:Envelope>";
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

    void processCreateCustomerWebBook(String typeId, String quickBookId) {
        try {
            CustomerRestDTO.CallBackEvent callBackEvent = new CustomerRestDTO.CallBackEvent();
            callBackEvent.setType("CREATE_CUSTOMER");
            callBackEvent.setTypeId(typeId);
            callBackEvent.setQuickBookId(quickBookId);
            adminServiceClient.createCustomerEventCallBack(callBackEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
