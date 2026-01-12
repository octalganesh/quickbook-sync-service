package com.octal.fsm.event.listener;

import com.octal.fsm.clients.AdminServiceClient;
import com.octal.fsm.event.CustomerSyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
public class CustomerSyncListener {

    @Autowired
    private AdminServiceClient adminServiceClient;

    @Async("customerSyncExecutor")
    @EventListener
    public void handleCustomerSync(CustomerSyncEvent event) {
        try {
            adminServiceClient.updateCustomer(event.getCreateQueue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
