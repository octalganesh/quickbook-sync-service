package com.octal.fsm.service.soap.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octal.fsm.dto.soap.CreateAccountDTO;
import com.octal.fsm.entities.QbdAccount;
import com.octal.fsm.repositories.QbdAccountRepository;
import com.octal.fsm.service.soap.CreateAccountService;
import com.octal.fsm.utils.ObjectOrArrayAdapter;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CreateAccountServiceImpl implements CreateAccountService {

    @Autowired
    private QbdAccountRepository accountRepository;

    @Override
    @Transactional
    public void createSyncAccountFromQuickBookWebConnector(String xmlPayload) throws Exception {
        String payloadJson = XmlUtil.convertXmlToJson(xmlPayload);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        new TypeToken<List<CreateAccountDTO.AccountRet>>() {}.getType(),
                        new ObjectOrArrayAdapter<>(CreateAccountDTO.AccountRet.class)
                )
                .create();

        CreateAccountDTO accountDTO = gson.fromJson(payloadJson, CreateAccountDTO.class);

        if (accountDTO.getQBXMLMsgsRs().getAccountQueryRs().getAccountRet()== null || accountDTO.getQBXMLMsgsRs().getAccountQueryRs().getAccountRet().isEmpty()) return;
        List<CreateAccountDTO.AccountRet> accounts = accountDTO.getQBXMLMsgsRs().getAccountQueryRs().getAccountRet();

        List<String> listIds = accounts.stream().map(a -> a.getListID().getValue())
                .collect(Collectors.toList());

        Map<String, QbdAccount> existingMap = accountRepository.findByListIdIn(listIds)
                        .stream().collect(Collectors.toMap(QbdAccount::getListId,
                                Function.identity()));

        List<QbdAccount> toSave = new ArrayList<>();

        for (CreateAccountDTO.AccountRet dto : accounts) {
            QbdAccount account = existingMap.getOrDefault(dto.getListID().getValue(), new QbdAccount());
            account.setListId(dto.getListID().getValue());
            account.setName(dto.getName() != null ? dto.getName().getValue() : null);
            account.setFullName(dto.getFullName() != null ? dto.getFullName().getValue() : null);
            account.setAccountType(dto.getAccountType() != null ? dto.getAccountType().getValue() : null);
            account.setAccountNumber(dto.getAccountNumber() != null ? dto.getAccountNumber().getValue() : null);
            account.setActive(dto.getIsActive() != null && Boolean.parseBoolean(dto.getIsActive().getValue()));
            account.setBalance(dto.getBalance() != null ? new BigDecimal(dto.getBalance().getValue()) : BigDecimal.ZERO);
            account.setTotalBalance(dto.getTotalBalance() != null ? new BigDecimal(dto.getTotalBalance().getValue()) : BigDecimal.ZERO);
            toSave.add(account);
        }
        accountRepository.saveAll(toSave);
    }

}
