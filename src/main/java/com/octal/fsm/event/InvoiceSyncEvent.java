package com.octal.fsm.event;

import com.octal.fsm.dto.rest.InvoiceRestDTO;

public class InvoiceSyncEvent {

    private final InvoiceRestDTO.Add createQueue;

    public InvoiceSyncEvent(InvoiceRestDTO.Add createQueue) {
        this.createQueue = createQueue;
    }

    public InvoiceRestDTO.Add getCreateQueue() {
        return createQueue;
    }
}
