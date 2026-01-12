package com.octal.fsm.utils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import java.io.StringReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import java.util.*;

public class XmlUtil {

    public static Document stringToXml(String xml) {
        try {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Document sourceToDocument(Source src) {
        return (Document) ((DOMSource) src).getNode();
    }

    public static Source stringSource(String xml) {
        return new DOMSource(stringToXml(xml));
    }


    /**
     * Convert QuickBooks SOAP response XML into JSON string
     * @param xml Outer XML (receiveResponseXML)
     * @return JSON string
     */
    public static String convertXmlToJson(String xml) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xml)));

            // Get the <response> node
            Element root = doc.getDocumentElement();
            Node responseNode = root.getElementsByTagName("response").item(0);
            String innerXml = responseNode.getTextContent();

            // Parse inner XML
            Document innerDoc = db.parse(new InputSource(new StringReader(innerXml)));
            Element qbxmlRoot = innerDoc.getDocumentElement();

            // Convert inner XML to Map
            Map<String, Object> jsonMap = parseElement(qbxmlRoot);

            // Convert Map to JSON
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonMap);

        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * Recursive helper method to parse XML Element into Map
     */
    private static Map<String, Object> parseElement(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        NodeList childNodes = element.getChildNodes();

        boolean hasElementChild = false;

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node instanceof Element) {
                hasElementChild = true;
                Element child = (Element) node;
                String nodeName = child.getNodeName();

                Map<String, Object> childMap = parseElement(child);

                // Handle repeated elements as list
                if (map.containsKey(nodeName)) {
                    Object existing = map.get(nodeName);
                    if (existing instanceof List) {
                        ((List<Object>) existing).add(childMap);
                    } else {
                        List<Object> list = new ArrayList<>();
                        list.add(existing);
                        list.add(childMap);
                        map.put(nodeName, list);
                    }
                } else {
                    map.put(nodeName, childMap);
                }
            }
        }

        // If there are no element children, return the text content as a map with a special key
        if (!hasElementChild) {
            String text = element.getTextContent().trim();
            map.put("value", text); // wrap text in a map
        }

        return map;
    }
}
