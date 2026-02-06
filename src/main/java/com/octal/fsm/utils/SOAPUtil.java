package com.octal.fsm.utils;

public class SOAPUtil {

    public static String receiveResponseXMLResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<receiveResponseXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "<receiveResponseXMLResult>100</receiveResponseXMLResult>" +
                "</receiveResponseXMLResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    public static String getLastErrorResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>No error</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    public static String closeConnectionResponse() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <closeConnectionResponse xmlns=\"http://developer.intuit.com/\">" +
                "      <closeConnectionResult>Customer Create successfully.</closeConnectionResult>" +
                "    </closeConnectionResponse>" +
                "  </soap:Body>" +
                "</soap:Envelope>";
    }

    public static String errorUnknownMethod() {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>Unknown method</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    public static String errorSOAP(String msg) {
        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<getLastErrorResponse xmlns=\"http://developer.intuit.com/\">" +
                "<getLastErrorResult>ERROR: " + msg + "</getLastErrorResult>" +
                "</getLastErrorResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }

    public static String buildSoapQbxmlEnvelope(String qbxmlVersion, String innerQbRequest) {

        return "<?xml version=\"1.0\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "<soap:Body>" +
                "<sendRequestXMLResponse xmlns=\"http://developer.intuit.com/\">" +
                "<sendRequestXMLResult><![CDATA[" +

                "<?qbxml version=\"" + qbxmlVersion + "\"?>" +
                "<QBXML>" +
                "<QBXMLMsgsRq onError=\"stopOnError\">" +

                innerQbRequest +

                "</QBXMLMsgsRq>" +
                "</QBXML>" +

                "]]></sendRequestXMLResult>" +
                "</sendRequestXMLResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";
    }
}
