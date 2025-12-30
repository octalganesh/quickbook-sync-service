package com.octal.supa.event.listener;

import com.octal.supa.clients.JobServiceClient;
import com.octal.supa.event.InventorySyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
public class InventorySyncListener {

    private final JobServiceClient jobServiceClient;

    @Async("inventoryExecutor")
    @EventListener
    public void handleInventorySync(InventorySyncEvent event) {
        try {
            jobServiceClient.saveInventory(event.getInventoryList(), event.getTenantId(), event.isSuperAdmin());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

