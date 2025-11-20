package com.octal.supa.dto;

import lombok.Data;

@Data
public class CreateCustomerResponse {
    private String ticket;
    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs{
        private CustomerAddRs CustomerAddRs;

        @Data
        public static class CustomerAddRs{
            private String statusCode;
            private String statusSeverity;
            private String statusMessage;
            private CustomerRet CustomerRet;

            @Data
            public static class CustomerRet{
                private String ListID;
            }
        }
    }

}
