package com.octal.fsm.service.rest.impl;

import com.octal.fsm.dto.rest.CustomerRestDTO;
import com.octal.fsm.entities.CreateCustomerQueue;
import com.octal.fsm.entities.CustomerType;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.repositories.CreateCustomerQueueRepository;
import com.octal.fsm.repositories.CustomerTypeRepository;
import com.octal.fsm.service.rest.CustomerRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerRestServiceImpl implements CustomerRestService {

    private final CreateCustomerQueueRepository createCustomerQueueRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    public CustomerRestServiceImpl(CreateCustomerQueueRepository createCustomerQueueRepository) {
        this.createCustomerQueueRepository = createCustomerQueueRepository;
    }

    @Override
    public String createCustomerQueue(CustomerRestDTO.CreateQueue createQueue) throws CodeException {
        Optional<CreateCustomerQueue> createCustomerQueueCheck = createCustomerQueueRepository.findByFullNameAndCustomerId(createQueue.getFullName(), createQueue.getCustomerId());
        if (createCustomerQueueCheck.isPresent())
            throw new CodeException("Customer is already queued.", ErrorCode.COMMON);
        CreateCustomerQueue createCustomerQueue = new CreateCustomerQueue();
        createCustomerQueue.setActive(createQueue.getActive());
        createCustomerQueue.setFullName(createQueue.getFullName());
        createCustomerQueue.setCustomerId(createQueue.getCustomerId());
        createCustomerQueue.setEmail(createQueue.getEmail());
        createCustomerQueue.setSecondaryEmail(createQueue.getSecondaryEmail());
        createCustomerQueue.setMobile(createQueue.getMobile());
        createCustomerQueue.setAlterNativeMobile1(createQueue.getAlterNativeMobile1());
        createCustomerQueue.setAlterNativeMobile2(createQueue.getAlterNativeMobile2());
        createCustomerQueue.setAddress(createQueue.getAddress());
        createCustomerQueue.setGender(createQueue.getGender());
        if(createQueue.getCustomerTypeName() != null){
            Optional<CustomerType> byName = customerTypeRepository.findByName(createQueue.getCustomerTypeName());
            byName.ifPresent(customerType -> createCustomerQueue.setCustomerTypeId(customerType.getListId()));
            createCustomerQueue.setCustomerTypeName(createQueue.getCustomerTypeName());
        }
        createCustomerQueue.setCustomerUuid(createQueue.getCustomerUuid());
        createCustomerQueue.setSyncStatus("QUEUE");
        CreateCustomerQueue save = createCustomerQueueRepository.save(createCustomerQueue);
        return save.getUuid();
    }
}
