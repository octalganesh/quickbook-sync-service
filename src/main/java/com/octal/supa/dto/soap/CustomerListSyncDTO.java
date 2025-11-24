package com.octal.supa.dto.soap;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CustomerListSyncDTO {
    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs{
        private CustomerQueryRs CustomerQueryRs;

        @Data
        public static class CustomerQueryRs{
            private ArrayList<CustomerRet> CustomerRet;

            @Data
            public static class CustomerRet{
                private CommonValue ListID;
                private CommonValue TimeCreated;
                private CommonValue TimeModified;
                private CommonValue EditSequence;
                private CommonValue Name;
                private CommonValue FullName;
                private CommonValue IsActive;
                private CommonValue Sublevel;
                private CommonValue Balance;
                private CommonValue TotalBalance;
                private CommonValue JobStatus;
                private CommonValue PreferredDeliveryMethod;

                @Data
                public static class CommonValue{
                    private String value;
                }

            }
        }
    }

}