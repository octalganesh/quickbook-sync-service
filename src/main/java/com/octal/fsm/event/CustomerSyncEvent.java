package com.octal.fsm.event;

import com.octal.fsm.dto.rest.CustomerRestDTO;

public class CustomerSyncEvent {

    private final CustomerRestDTO.CreateQueue createQueue;

    public CustomerSyncEvent(CustomerRestDTO.CreateQueue createQueue) {
        this.createQueue = createQueue;
    }

    public CustomerRestDTO.CreateQueue getCreateQueue() {
        return createQueue;
    }


}
