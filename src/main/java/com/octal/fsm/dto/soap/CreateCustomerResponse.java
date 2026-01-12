package com.octal.fsm.dto.soap;

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
                private String TimeCreated;
                private String TimeModified;
                private String EditSequence;
                private String Name;
                private String FullName;
                private String IsActive;
                private String Sublevel;
                private String FirstName;
                private String Balance;
                private String TotalBalance;
                private String JobStatus;
                private String PreferredDeliveryMethod;
            }
        }
    }

}
