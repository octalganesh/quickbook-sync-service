package com.octal.supa.event.listener;

import com.octal.supa.clients.JobServiceClient;
import com.octal.supa.event.InvoiceSyncEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableAsync
public class InvoiceSyncListener {

    @Autowired
    private JobServiceClient jobServiceClient;

    @Async("invoiceSyncExecutor")
    @EventListener
    public void handleInvoiceSync(InvoiceSyncEvent event) {
        try {
            jobServiceClient.updateInvoiceDetails(event.getCreateQueue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
