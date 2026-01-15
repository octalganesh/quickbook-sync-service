package com.octal.fsm.dto.soap;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoiceDTO {

    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs {
        private InvoiceQueryRs InvoiceQueryRs;
    }

    @Data
    public static class InvoiceQueryRs {
        private List<InvoiceRet> InvoiceRet;
    }

    @Data
    public static class InvoiceRet {
        private QBValue<String> TxnID;
        private QBValue<String> RefNumber;
        private CustomerRef CustomerRef;
        private QBValue<BigDecimal> Subtotal;//total amount without tax
        private QBValue<BigDecimal> BalanceRemaining;//partial payment then remaining amount
        private QBValue<BigDecimal> SalesTaxTotal;//tax on subTotal amount
        private QBValue<BigDecimal> SalesTaxPercentage;//tas percentage
        private QBValue<Boolean> IsPaid;//all amount paid
    }

    @Data
    public static class CustomerRef {
        private QBValue<String> FullName;
    }
}

