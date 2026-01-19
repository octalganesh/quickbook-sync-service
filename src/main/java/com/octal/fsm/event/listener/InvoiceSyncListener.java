package com.octal.fsm.event.listener;

import com.octal.fsm.clients.JobServiceClient;
import com.octal.fsm.dto.rest.InvoiceRestDTO;
import com.octal.fsm.entities.CreateInvoiceQueue;
import com.octal.fsm.event.InvoiceSyncEvent;
import com.octal.fsm.repositories.CreateInvoiceQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@EnableAsync
public class InvoiceSyncListener {

    @Autowired
    private JobServiceClient jobServiceClient;
    @Autowired
    private CreateInvoiceQueueRepository createInvoiceQueueRepository;

    @Async("invoiceSyncExecutor")
    @EventListener
    public void handleInvoiceSync(InvoiceSyncEvent event) {
        try {
            if(event.getAddList() == null && event.getCreateQueue() != null){
                jobServiceClient.updateInvoiceDetails(event.getCreateQueue());
            }else if(event.getAddList() != null && !event.getAddList().isEmpty()){
                List<String> refIds =  event.getAddList().stream()
                                .map(i -> i.getRefId() != null ? i.getRefId() : null)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                Map<String, CreateInvoiceQueue> queueMap =
                        createInvoiceQueueRepository.findByRefIdIn(refIds)
                                .stream()
                                .collect(Collectors.toMap(CreateInvoiceQueue::getRefId, q -> q));

                List<CreateInvoiceQueue> toSave = new ArrayList<>();
                for (InvoiceRestDTO.Add add : event.getAddList()) {
                    String refId = add.getRefId();
                    if (refId == null) continue;

                    CreateInvoiceQueue queue = queueMap.get(refId);
                    if (queue == null) continue;

                    queue.setIsPaid(add.getIsPaid());
                    queue.setBalanceDue(add.getBalanceDue());
                    queue.setTotalAmountWithTax(add.getTotalAmountWithTax());
                    toSave.add(queue);
                }
                if (!toSave.isEmpty()) {
                    createInvoiceQueueRepository.saveAll(toSave);
                }
                jobServiceClient.updateInvoiceDetailsList(event.getAddList());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
