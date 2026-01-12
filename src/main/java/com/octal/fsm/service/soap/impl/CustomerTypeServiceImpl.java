package com.octal.fsm.service.soap.impl;

import com.google.gson.Gson;
import com.octal.fsm.dto.soap.CustomerTypeSyncDTO;
import com.octal.fsm.entities.CustomerType;
import com.octal.fsm.repositories.CustomerTypeRepository;
import com.octal.fsm.service.soap.CustomerTypeService;
import com.octal.fsm.utils.TextUtils;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomerTypeServiceImpl implements CustomerTypeService {


    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Override
    public void syncCustomerTypeFromQuickBookWebConnector(String xmlPayload) {
        try {
            String payloadJson = XmlUtil.convertXmlToJson(xmlPayload);
            if (xmlPayload.contains("CustomerTypeQueryRs")) {
                Gson gson = new Gson();
                CustomerTypeSyncDTO customerTypeSyncDTO = gson.fromJson(payloadJson, CustomerTypeSyncDTO.class);
                if (customerTypeSyncDTO != null) {
                    if (customerTypeSyncDTO.getQBXMLMsgsRs() != null &&
                            customerTypeSyncDTO.getQBXMLMsgsRs().getCustomerTypeQueryRs() != null &&
                            customerTypeSyncDTO.getQBXMLMsgsRs().getCustomerTypeQueryRs().getCustomerTypeRet() != null) {
                        List<CustomerType> listOfCustomerTypes = new ArrayList<>();
                        for (CustomerTypeSyncDTO.QBXMLMsgsRs.CustomerTypeQueryRs.CustomerTypeRet customerTypeRet : customerTypeSyncDTO.getQBXMLMsgsRs().getCustomerTypeQueryRs().getCustomerTypeRet()) {
                            if (customerTypeRet.getListID() != null &&
                                    !TextUtils.isEmpty(customerTypeRet.getListID().getValue()) &&
                                    customerTypeRet.getName() != null && !TextUtils.isEmpty(customerTypeRet.getName().getValue()) &&
                                    customerTypeRet.getFullName() != null && !TextUtils.isEmpty(customerTypeRet.getFullName().getValue()) &&
                                    customerTypeRet.getIsActive() != null && !TextUtils.isEmpty(customerTypeRet.getIsActive().getValue())) {
                                CustomerType customerType;
                                Optional<CustomerType> customerTypeCheck = customerTypeRepository.findByListId(customerTypeRet.getListID().getValue());
                                customerType = customerTypeCheck.orElseGet(CustomerType::new);
                                customerType.setListId(customerTypeRet.getListID().getValue());
                                customerType.setName(customerTypeRet.getName().getValue());
                                customerType.setFullName(customerTypeRet.getFullName().getValue());
                                customerType.setIsActive(customerTypeRet.getIsActive().getValue());
                                listOfCustomerTypes.add(customerType);
                            }
                        }
                        customerTypeRepository.saveAll(listOfCustomerTypes);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
