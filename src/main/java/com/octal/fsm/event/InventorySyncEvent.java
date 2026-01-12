package com.octal.fsm.event;

import com.octal.fsm.dto.rest.InventoryRequestDTO;

import java.util.List;

public class InventorySyncEvent {

    private final List<InventoryRequestDTO.Add> inventoryList;
    private final Long tenantId;
    private final boolean isSuperAdmin;

    public InventorySyncEvent(List<InventoryRequestDTO.Add> inventoryList, Long tenantId, boolean isSuperAdmin) {
        this.inventoryList = inventoryList;
        this.tenantId = tenantId;
        this.isSuperAdmin = isSuperAdmin;
    }

    public List<InventoryRequestDTO.Add> getInventoryList() {
        return inventoryList;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }
}

