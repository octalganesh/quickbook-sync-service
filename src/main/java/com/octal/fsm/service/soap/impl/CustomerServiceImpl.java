package com.octal.fsm.service.soap.impl;

import com.google.gson.Gson;
import com.octal.fsm.clients.AdminServiceClient;
import com.octal.fsm.dto.rest.CustomerDTO;
import com.octal.fsm.dto.soap.CreateCustomerResponse;
import com.octal.fsm.dto.soap.CustomerListSyncDTO;
import com.octal.fsm.entities.Customers;
import com.octal.fsm.repositories.CustomerRepository;
import com.octal.fsm.service.soap.CustomerService;
import com.octal.fsm.utils.TextUtils;
import com.octal.fsm.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminServiceClient adminServiceClient;

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
                                if (customerRet.getPhone() != null && !TextUtils.isEmpty(customerRet.getPhone().getValue()))
                                    customers.setPhoneNumber(customerRet.getPhone().getValue());

                                if (customerRet.getEmail() != null && !TextUtils.isEmpty(customerRet.getEmail().getValue())) {
                                    customers.setEmail(customerRet.getEmail().getValue());
                                }
                                if (customerRet.getCustomerTypeRef() != null) {
                                    CustomerListSyncDTO.QBXMLMsgsRs.CustomerQueryRs.CustomerRet.CustomerTypeRef typeRef = customerRet.getCustomerTypeRef();
                                    if (typeRef.getFullName() != null) {
                                        customers.setCustomerTypeName(typeRef.getFullName().getValue());
                                    }
                                    if (typeRef.getListID() != null) {
                                        customers.setCustomerTypeId(typeRef.getListID().getValue());
                                    }
                                }
                                customers.setPreferredDeliveryMethod(Objects.nonNull(customerRet.getPreferredDeliveryMethod()) ? customerRet.getPreferredDeliveryMethod().getValue() : null);
                                listOfCustomer.add(customers);
                            }
                        }
                        customerRepository.saveAll(listOfCustomer);
                        syncCustomerFromQBDtoLocal(listOfCustomer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncCustomerFromQBDtoLocal(List<Customers> customers) throws Exception {
        try {
            if (customers != null && !customers.isEmpty()) {
                List<CustomerDTO> customerDTOList = customers.stream()
                        .map(this::mapToCustomerDTO)
                        .collect(Collectors.toList());
                adminServiceClient.createCustomer(customerDTOList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CustomerDTO mapToCustomerDTO(Customers customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setListId(customer.getListId());
        dto.setName(customer.getName());
        dto.setFullName(customer.getFullName());
        dto.setIsActive(customer.getIsActive());
        dto.setTimeCreated(customer.getTimeCreated());
        dto.setTimeModified(customer.getTimeModified());
        dto.setEditSequence(customer.getEditSequence());
        dto.setSublevel(customer.getSublevel());
        dto.setBalance(customer.getBalance());
        dto.setTotalBalance(customer.getTotalBalance());
        dto.setJobStatus(customer.getJobStatus());
        if (customer.getEmail() != null)
            dto.setEmail(customer.getEmail());
        if (customer.getCustomerTypeName() != null)
            dto.setCustomerTypeName(customer.getCustomerTypeName());
        if (customer.getPhoneNumber() != null)
            dto.setMobileNo(customer.getPhoneNumber());
        dto.setPreferredDeliveryMethod(customer.getPreferredDeliveryMethod());
        return dto;
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
