package com.octal.fsm.dto.soap;

import lombok.Data;

import java.util.List;

@Data
public class CreateAccountDTO {

    private QBXMLMsgsRs QBXMLMsgsRs;

    @Data
    public static class QBXMLMsgsRs {
        private AccountQueryRs AccountQueryRs;
    }

    @Data
    public static class AccountQueryRs {
        private List<AccountRet> AccountRet;
    }

    @Data
    public static class AccountRet {
        private ListID ListID;
        private Name Name;
        private FullName FullName;
        private IsActive IsActive;
        private AccountType AccountType;
        private AccountNumber AccountNumber;
        private Balance Balance;
        private TotalBalance TotalBalance;
    }

    @Data public static class ListID { private String value; }
    @Data public static class Name { private String value; }
    @Data public static class FullName { private String value; }
    @Data public static class IsActive { private String value; }
    @Data public static class AccountType { private String value; }
    @Data public static class AccountNumber { private String value; }
    @Data public static class Balance { private String value; }
    @Data public static class TotalBalance { private String value; }

}
