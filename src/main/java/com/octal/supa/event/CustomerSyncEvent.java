package com.octal.supa.event;

import com.octal.supa.dto.rest.CustomerRestDTO;
import com.octal.supa.dto.rest.InventoryRequestDTO;

import java.util.List;

public class CustomerSyncEvent {

    private final CustomerRestDTO.CreateQueue createQueue;

    public CustomerSyncEvent(CustomerRestDTO.CreateQueue createQueue) {
        this.createQueue = createQueue;
    }

    public CustomerRestDTO.CreateQueue getCreateQueue() {
        return createQueue;
    }


}
