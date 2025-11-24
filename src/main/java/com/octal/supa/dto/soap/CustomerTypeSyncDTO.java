package com.octal.supa.dto.soap;

import lombok.Data;

import java.util.ArrayList;

@Data
public class CustomerTypeSyncDTO {
    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs{
        private CustomerTypeQueryRs CustomerTypeQueryRs;

        @Data
        public static class CustomerTypeQueryRs{
            private ArrayList<CustomerTypeRet> CustomerTypeRet;

            @Data
            public static class CustomerTypeRet{
                private CommonValue ListID;
                private CommonValue TimeCreated;
                private CommonValue TimeModified;
                private CommonValue EditSequence;
                private CommonValue Name;
                private CommonValue FullName;
                private CommonValue IsActive;
                private CommonValue Sublevel;

                @Data
                public static class CommonValue{
                    private String value;
                }

            }
        }
    }

}