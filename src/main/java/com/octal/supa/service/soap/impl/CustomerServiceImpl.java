package com.octal.supa.service.soap.impl;

import com.google.gson.Gson;
import com.octal.supa.dto.soap.CreateCustomerResponse;
import com.octal.supa.dto.soap.CustomerListSyncDTO;
import com.octal.supa.entities.Customers;
import com.octal.supa.repositories.CustomerRepository;
import com.octal.supa.service.soap.CustomerService;
import com.octal.supa.utils.TextUtils;
import com.octal.supa.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public void syncCustomersFromQuickBookWebConnector(String xmlPayload) {
        try {
            String payloadJson = XmlUtil.convertXmlToJson(xmlPayload);
            if (xmlPayload.contains("CustomerQueryRs")) {
                Gson gson = new Gson();
                CustomerListSyncDTO customerListSyncDTO = gson.fromJson(payloadJson, CustomerListSyncDTO.class);
                if (customerListSyncDTO != null) {
                    if (customerListSyncDTO.getQBXMLMsgsRs() != null &&
                            customerListSyncDTO.getQBXMLMsgsRs().getCustomerQueryRs() != null &&
                            customerListSyncDTO.getQBXMLMsgsRs().getCustomerQueryRs().getCustomerRet() != null) {
                        List<Customers> listOfCustomer = new ArrayList<>();
                        for (CustomerListSyncDTO.QBXMLMsgsRs.CustomerQueryRs.CustomerRet customerRet : customerListSyncDTO.getQBXMLMsgsRs().getCustomerQueryRs().getCustomerRet()) {
                            if (customerRet.getListID() != null && !TextUtils.isEmpty(customerRet.getListID().getValue())) {
                                Customers customers;
                                Optional<Customers> customerCheck = customerRepository.findByListId(customerRet.getListID().getValue());
                                customers = customerCheck.orElseGet(Customers::new);
                                customers.setListId(customerRet.getListID().getValue());
                                customers.setName(customerRet.getName().getValue());
                                customers.setFullName(customerRet.getFullName().getValue());
                                customers.setIsActive(customerRet.getIsActive().getValue());
                                customers.setTimeCreated(customerRet.getTimeCreated().getValue());
                                customers.setTimeModified(customerRet.getTimeModified().getValue());
                                customers.setEditSequence(customerRet.getEditSequence().getValue());
                                customers.setSublevel(customerRet.getSublevel().getValue());
                                customers.setBalance(customerRet.getBalance().getValue());
                                customers.setTotalBalance(customerRet.getTotalBalance().getValue());
                                customers.setJobStatus(customerRet.getJobStatus().getValue());
                                customers.setPreferredDeliveryMethod(Objects.nonNull(customerRet.getPreferredDeliveryMethod())?customerRet.getPreferredDeliveryMethod().getValue():null);
                                listOfCustomer.add(customers);
                            }
                        }
                        customerRepository.saveAll(listOfCustomer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processCustomerIntoDB(CreateCustomerResponse.QBXMLMsgsRs.CustomerAddRs.CustomerRet customerRet) {
        if (!TextUtils.isEmpty(customerRet.getListID())) {
            Customers customers;
            Optional<Customers> customerCheck = customerRepository.findByListId(customerRet.getListID());
            customers = customerCheck.orElseGet(Customers::new);
            customers.setListId(customerRet.getListID());
            customers.setName(customerRet.getName());
            customers.setFullName(customerRet.getFullName());
            customers.setIsActive(customerRet.getIsActive());
            customers.setTimeCreated(customerRet.getTimeCreated());
            customers.setTimeModified(customerRet.getTimeModified());
            customers.setEditSequence(customerRet.getEditSequence());
            customers.setSublevel(customerRet.getSublevel());
            customers.setBalance(customerRet.getBalance());
            customers.setTotalBalance(customerRet.getTotalBalance());
            customers.setJobStatus(customerRet.getJobStatus());
            customers.setPreferredDeliveryMethod(customerRet.getPreferredDeliveryMethod());
            customerRepository.save(customers);
        }
    }
}
