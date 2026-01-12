package com.octal.fsm.dto.soap;

import lombok.Data;

@Data
public class CreateInvoiceResponse {
    private String ticket;
    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs{
        private InvoiceAddRs InvoiceAddRs;

        @Data
        public static class InvoiceAddRs{
            private String statusCode;
            private String statusSeverity;
            private String statusMessage;
            private InvoiceRet InvoiceRet;

            @Data
            public static class InvoiceRet{
                private String ListID;
                private String TxnID;
                private String TimeCreated;
                private String TimeModified;
                private String EditSequence;
                private String TxnNumber;
                private CustomerRef CustomerRef;
                private String RefNumber;
                private String Subtotal;
                private String BalanceRemaining;
                private String SalesTaxTotal;
                private String SalesTaxPercentage;

                @Data
                public static class CustomerRef{
                    private String ListID;
                    private String FullName;
                }
            }
        }
    }

}
