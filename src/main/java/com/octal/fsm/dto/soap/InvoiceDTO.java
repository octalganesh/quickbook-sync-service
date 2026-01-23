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
        private ReceivePaymentQueryRs ReceivePaymentQueryRs;
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
    public static class ReceivePaymentQueryRs {
        private List<ReceivePaymentRet> ReceivePaymentRet;
    }

    @Data
    public static class ReceivePaymentRet {
        private QBValue<String> TxnID;
        private PaymentMethodRef PaymentMethodRef;
        private List<AppliedToTxnRet> AppliedToTxnRet;
    }

    @Data
    public static class PaymentMethodRef {
        private QBValue<String> FullName; // Cash / Card / Check
    }

    @Data
    public static class AppliedToTxnRet {
        private QBValue<String> TxnID; // Invoice TxnID
    }

    @Data
    public static class LinkedTxn {
        private QBValue<String> TxnID;
        private QBValue<String> TxnType; // ReceivePayment
    }

    @Data
    public static class CustomerRef {
        private QBValue<String> FullName;
    }
}

