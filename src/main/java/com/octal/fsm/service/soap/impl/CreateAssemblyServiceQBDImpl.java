package com.octal.fsm.service.soap.impl;

import com.octal.fsm.entities.CreateAssemblyComponent;
import com.octal.fsm.entities.CreateAssemblyQueue;
import com.octal.fsm.repositories.CreateAssemblyQueueRepository;
import com.octal.fsm.service.soap.CreateAssemblyServiceQBD;
import com.octal.fsm.utils.SOAPUtil;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class CreateAssemblyServiceQBDImpl implements CreateAssemblyServiceQBD {

    @Autowired
    private CreateAssemblyQueueRepository assemblyQueueRepository;

    @Override
    public String getSyncAuthToken() {
        assemblyQueueRepository.resetAllActiveTokens();
        CreateAssemblyQueue createAssemblyQueue = assemblyQueueRepository.findNextQueued(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
        if (createAssemblyQueue != null) {
            createAssemblyQueue.setActiveToken(createAssemblyQueue.getUuid());
            assemblyQueueRepository.save(createAssemblyQueue);
            return authenticateResponse(createAssemblyQueue.getUuid());
        } else {
            return authenticateResponse("");
        }
    }

    @Override
    public String syncAssemblyFromQueue() {
        CreateAssemblyQueue queue = assemblyQueueRepository
                .findNextQueued(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);

        if (queue == null) {
            return emptySoapResponse();
        }
        // 🔹 Build inner QBXML only
        String assemblyAddRq = buildInventoryAssemblyAddRq(queue);
        // 🔹 Wrap with common SOAP + QBXML envelope
        String qbxml = SOAPUtil.buildSoapQbxmlEnvelope("17.0", assemblyAddRq);
        queue.setActiveToken(null);
        assemblyQueueRepository.save(queue);
        return qbxml;
    }

    private String buildInventoryAssemblyAddRq(CreateAssemblyQueue queue) {

        return "<ItemInventoryAssemblyAddRq>" +
                "<ItemInventoryAssemblyAdd>" +

                "<Name>" + escapeXml(queue.getAssemblyName()) + "</Name>" +

                "<SalesPrice>" + queue.getSalesPrice() + "</SalesPrice>" +

                "<IncomeAccountRef><FullName>" +
                escapeXml(queue.getIncomeAccount()) +
                "</FullName></IncomeAccountRef>" +

                "<COGSAccountRef><FullName>" +
                escapeXml(queue.getCogsAccount()) +
                "</FullName></COGSAccountRef>" +

                "<AssetAccountRef><FullName>" +
                escapeXml(queue.getAssetAccount()) +
                "</FullName></AssetAccountRef>" +

                buildAssemblyLineMod(queue) +

                "</ItemInventoryAssemblyAdd>" +
                "</ItemInventoryAssemblyAddRq>";
    }


    @Override
    @Transactional
    public void createSyncAssemblyFromQBWC(String xmlPayload) throws Exception {
        String qbxmlEscaped = extractTag(xmlPayload, "response");
        if (qbxmlEscaped == null || qbxmlEscaped.isBlank()) {
            System.out.println("No QBXML response found");
            return;
        }
        // Step 2: Unescape XML entities
        String qbxml = unescapeXml(qbxmlEscaped);

        // Step 3: Parse QBXML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(qbxml)));

        // Step 4: Extract values
        String listId = getTagValue(doc, "ListID");
        String editSequence = getTagValue(doc, "EditSequence");
        String name = getTagValue(doc, "Name");
        assemblyQueueRepository.updateAfterCreate(listId,editSequence,name);
    }

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

    private String buildAssemblyLineMod(CreateAssemblyQueue queue) {
        StringBuilder sb = new StringBuilder();
        for (CreateAssemblyComponent c : queue.getComponents()) {
            sb.append("<ItemInventoryAssemblyLine>")
                    .append("<ItemInventoryRef><FullName>")
                    .append(escapeXml(c.getItemName()))
                    .append("</FullName></ItemInventoryRef>")
                    .append("<Quantity>")
                    .append(c.getQuantity().intValue())
                    .append("</Quantity>")
                    .append("</ItemInventoryAssemblyLine>");

        }
        return sb.toString();
    }

    private String escapeXml(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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
    private String extractTag(String xml, String tag) {
        int start = xml.indexOf("<" + tag + ">");
        int end = xml.indexOf("</" + tag + ">");
        if (start == -1 || end == -1) return null;
        return xml.substring(start + tag.length() + 2, end);
    }
    private String unescapeXml(String xml) {
        return xml
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&apos;", "'");
    }
    private String getTagValue(Document doc, String tagName) {
        NodeList list = doc.getElementsByTagName(tagName);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent();
    }


}
