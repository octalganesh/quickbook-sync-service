package com.octal.fsm.event;

import com.octal.fsm.dto.rest.InventoryRequestDTO;
import com.octal.fsm.dto.rest.InvoiceRestDTO;

import java.util.List;

public class InvoiceSyncEvent {

    private final InvoiceRestDTO.Add createQueue;
    private final List<InvoiceRestDTO.Add> addList;

    public InvoiceSyncEvent(InvoiceRestDTO.Add createQueue, List<InvoiceRestDTO.Add> addList) {
        this.createQueue = createQueue;
        this.addList = addList;
    }

    public List<InvoiceRestDTO.Add> getAddList() {
        return addList;
    }

    public InvoiceRestDTO.Add getCreateQueue() {
        return createQueue;
    }
}
