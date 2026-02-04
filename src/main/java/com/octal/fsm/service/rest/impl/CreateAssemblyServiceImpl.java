package com.octal.fsm.service.rest.impl;

import com.octal.fsm.dto.rest.AssemblyCreateRequest;
import com.octal.fsm.entities.CreateAssemblyComponent;
import com.octal.fsm.entities.CreateAssemblyQueue;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.repositories.CreateAssemblyQueueRepository;
import com.octal.fsm.service.rest.CreateAssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateAssemblyServiceImpl implements CreateAssemblyService {

    @Autowired
    private CreateAssemblyQueueRepository repository;

    @Override
    public void saveAssemblyQueue(AssemblyCreateRequest.AssemblyComponentAdd request) throws CodeException {
        try{
            if (repository.existsByAssemblyName(request.getName())) {
                throw new CodeException("Assembly with name "+request.getName()+" already exist.", ErrorCode.COMMON);
            }
            CreateAssemblyQueue queue = new CreateAssemblyQueue();
            queue.setAssemblyName(request.getName());
            queue.setIncomeAccount(request.getIncomeAccount());
            queue.setCogsAccount(request.getCogsAccount());
            queue.setAssetAccount(request.getAssetAccount());
            queue.setRequestId(request.getRequestId());
            queue.setSalesPrice(request.getSalesPrice());
            queue.setSyncStatus("QUEUED");
            queue.setActiveToken(null);
            for (AssemblyCreateRequest.AssemblyComponentDto dto : request.getComponents()) {
                CreateAssemblyComponent component = new CreateAssemblyComponent();
                component.setItemName(dto.getItemName());
                component.setQuantity(dto.getQuantity());
                component.setAssemblyQueue(queue);
                queue.getComponents().add(component);
            }
            repository.save(queue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
