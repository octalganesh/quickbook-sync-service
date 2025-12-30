package com.octal.supa.event;

import com.octal.supa.dto.rest.CustomerRestDTO;
import com.octal.supa.dto.rest.InvoiceRestDTO;

public class InvoiceSyncEvent {

    private final InvoiceRestDTO.Add createQueue;

    public InvoiceSyncEvent(InvoiceRestDTO.Add createQueue) {
        this.createQueue = createQueue;
    }

    public InvoiceRestDTO.Add getCreateQueue() {
        return createQueue;
    }
}
