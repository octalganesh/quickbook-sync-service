package com.octal.fsm.service.soap.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octal.fsm.clients.JobServiceClient;
import com.octal.fsm.dto.rest.InventoryRequestDTO;
import com.octal.fsm.dto.soap.InventoryPartDTO;
import com.octal.fsm.entities.InventoryPart;
import com.octal.fsm.event.InventorySyncEvent;
import com.octal.fsm.exceptions.CodeException;
import com.octal.fsm.exceptions.ErrorCode;
import com.octal.fsm.repositories.InventoryPartRepository;
import com.octal.fsm.service.soap.InventoryPartService;
import com.octal.fsm.utils.ObjectOrArrayAdapter;
import com.octal.fsm.utils.XmlUtil;
import io.netty.handler.codec.CodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class InventoryPartServiceImpl implements InventoryPartService {

    @Autowired
    private InventoryPartRepository inventoryPartRepository;
    @Autowired
    private JobServiceClient jobServiceClient;

    private static final int BATCH_SIZE = 500;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void syncItemFromQuickBookWebConnector(String xmlPayload) throws Exception {
        String payloadJson = XmlUtil.convertXmlToJson(xmlPayload);
        if (xmlPayload.contains("ItemInventoryRet")) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<InventoryPartDTO.ItemInventoryRet>>() {
                            }.getType(),
                            new ObjectOrArrayAdapter<>(InventoryPartDTO.ItemInventoryRet.class))
                    .create();
            InventoryPartDTO itemInventoryPartDTO = gson.fromJson(payloadJson, InventoryPartDTO.class);
            List<InventoryPart> listOfInventoryParts = new ArrayList<>();
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemDiscountQueryRs().getItemDiscountRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemInventoryAssemblyQueryRs().getItemInventoryAssemblyRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemInventoryQueryRs().getItemInventoryRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemNonInventoryQueryRs().getItemNonInventoryRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemOtherChargeQueryRs().getItemOtherChargeRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemSalesTaxQueryRs().getItemSalesTaxRet(), listOfInventoryParts);
            processItems(itemInventoryPartDTO.getQBXMLMsgsRs().getItemServiceQueryRs().getItemServiceRet(), listOfInventoryParts);
            List<InventoryPart> inventoryParts = inventoryPartRepository.saveAll(listOfInventoryParts);
            try {
                List<InventoryRequestDTO.Add> responseList = inventoryParts.stream()
                        .map(this::convertDTO).collect(Collectors.toList());
                eventPublisher.publishEvent(new InventorySyncEvent(responseList, 1L, true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void syncInventoryToJobService() throws Exception {
        try {
            List<InventoryPart> inventoryParts = inventoryPartRepository.findAll();
            if (inventoryParts.isEmpty()) {
                return;
            }
            List<InventoryRequestDTO.Add> batch = new ArrayList<>();
            for (InventoryPart part : inventoryParts) {
                batch.add(convertDTO(part));
                if (batch.size() == BATCH_SIZE) {
                    sendBatch(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                sendBatch(batch);
            }
        } catch (Exception e) {
            throw new CodeException("Failed to sync inventory to job service", ErrorCode.COMMON);
        }
    }

    @Override
    public void updateInventoryQuantities(List<InventoryRequestDTO.Add> addList) throws CodecException {
        if (addList == null || addList.isEmpty()) {
            return;
        }
        List<String> listIds = addList.stream()
                .map(InventoryRequestDTO.Add::getListId)
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<String, InventoryPart> inventoryMap =
                inventoryPartRepository.findByListIdIn(listIds)
                        .stream()
                        .collect(Collectors.toMap(InventoryPart::getListId, Function.identity()));

        for (InventoryRequestDTO.Add dto : addList) {
            InventoryPart inventory = inventoryMap.get(dto.getListId());
            if (inventory == null) {
                continue;
            }
            if (dto.getQuantityOnHand() != null) {
                inventory.setQuantityOnHand(dto.getQuantityOnHand());
            }
            if (dto.getQuantityOnOrder() != null) {
                inventory.setQuantityOnOrder(dto.getQuantityOnOrder());
            }
            if (dto.getQuantityOnSalesOrder() != null) {
                inventory.setQuantityOnSalesOrder(dto.getQuantityOnSalesOrder());
            }
            inventory.setUpdatedAt(LocalDateTime.now());
        }
        inventoryPartRepository.saveAll(inventoryMap.values());

    }

    private void sendBatch(List<InventoryRequestDTO.Add> batch) {
        try {
            jobServiceClient.saveInventory(batch, 1L, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processItems(List<InventoryPartDTO.ItemInventoryRet> ItemInventoryRet, List<InventoryPart> listOfInventoryParts) {
        for (InventoryPartDTO.ItemInventoryRet item : ItemInventoryRet) {
            try {
                InventoryPart inventoryPart;
                Optional<InventoryPart> inventoryPartCheck = inventoryPartRepository.findByListId(item.getListID() != null ? item.getListID().getValue() : null);
                if (inventoryPartCheck.isEmpty()) {
                    inventoryPart = new InventoryPart();
                    inventoryPart.setListId(item.getListID() != null ? item.getListID().getValue() : null);
                } else {
                    inventoryPart = inventoryPartCheck.get();
                }
                inventoryPart.setTimeCreated(item.getTimeCreated() != null ? item.getTimeCreated().toString() : null);
                inventoryPart.setTimeModified(item.getTimeModified() != null ? item.getTimeModified().toString() : null);
                inventoryPart.setEditSequence(item.getEditSequence() != null ? item.getEditSequence().getValue() : null);
                inventoryPart.setName(item.getName() != null ? item.getName().getValue() : null);
                inventoryPart.setFullName(item.getFullName() != null ? item.getFullName().getValue() : null);
                inventoryPart.setIsActive(item.getIsActive() != null ? item.getIsActive().getValue() : null);
                inventoryPart.setSublevel(item.getSublevel() != null ? item.getSublevel().getValue() : null);
                inventoryPart.setSalesTaxCodeListId(item.getSalesTaxCodeRef() != null && item.getSalesTaxCodeRef().getListID() != null ? item.getSalesTaxCodeRef().getListID().getValue() : null);
                inventoryPart.setSalesTaxCodeFullName(item.getSalesTaxCodeRef() != null && item.getSalesTaxCodeRef().getFullName() != null ? item.getSalesTaxCodeRef().getFullName().getValue() : null);
                inventoryPart.setSalesOrPurchasePrice(item.getSalesOrPurchase() != null ? item.getSalesOrPurchase().getPrice() != null ? item.getSalesOrPurchase().getPrice().getValue() : null : null);
                inventoryPart.setSalesOrPurchaseAccountFullName(item.getSalesOrPurchase() != null ? item.getSalesOrPurchase().getAccountRef() != null && item.getSalesOrPurchase().getAccountRef().getFullName() != null ? item.getSalesOrPurchase().getAccountRef().getFullName().getValue() : null : null);
                inventoryPart.setSalesOrPurchaseAccountListId(item.getSalesOrPurchase() != null ? item.getSalesOrPurchase().getAccountRef() != null && item.getSalesOrPurchase().getAccountRef().getListID() != null ? item.getSalesOrPurchase().getAccountRef().getListID().getValue() : null : null);
                inventoryPart.setDiscountRatePercentage(item.getDiscountRatePercent() != null ? item.getDiscountRatePercent().getValue() : null);
                inventoryPart.setAccountListId(item.getAccountRef() != null ? item.getAccountRef().getListID() != null ? item.getAccountRef().getListID().getValue() : null : null);
                inventoryPart.setAccountFullName(item.getAccountRef() != null ? item.getAccountRef().getFullName() != null ? item.getAccountRef().getFullName().getValue() : null : null);
                inventoryPart.setItemDesc(item.getItemDesc() != null ? item.getItemDesc().getValue() : null);
                inventoryPart.setTaxRate(item.getTaxRate() != null ? item.getTaxRate().getValue() : null);
                inventoryPart.setTaxVendorListId(item.getTaxVendorRef() != null ? item.getTaxVendorRef().getListID() != null ? item.getTaxVendorRef().getListID().getValue() : null : null);
                inventoryPart.setTaxVendorFullName(item.getTaxVendorRef() != null ? item.getTaxVendorRef().getFullName() != null ? item.getTaxVendorRef().getFullName().getValue() : null : null);
                inventoryPart.setSalePrice(item.getSalesPrice() != null ? item.getSalesPrice().getValue() : null);
                inventoryPart.setIncomeAccountListId(item.getIncomeAccountRef() != null && item.getIncomeAccountRef().getListID() != null ? item.getIncomeAccountRef().getListID().getValue() : null);
                inventoryPart.setIncomeAccountFullName(item.getIncomeAccountRef() != null && item.getIncomeAccountRef().getFullName() != null ? item.getIncomeAccountRef().getFullName().getValue() : null);
                inventoryPart.setCogsAccountListId(item.getCOGSAccountRef() != null && item.getCOGSAccountRef().getListID() != null ? item.getCOGSAccountRef().getListID().getValue() : null);
                inventoryPart.setCogsAccountFullName(item.getCOGSAccountRef() != null && item.getCOGSAccountRef().getFullName() != null ? item.getCOGSAccountRef().getFullName().getValue() : null);
                inventoryPart.setPrefVendorListId(item.getPrefVendorRef() != null && item.getPrefVendorRef().getListID() != null ? item.getPrefVendorRef().getListID().getValue() : null);
                inventoryPart.setPrefVendorFullName(item.getPrefVendorRef() != null && item.getPrefVendorRef().getFullName() != null ? item.getPrefVendorRef().getFullName().getValue() : null);
                inventoryPart.setAssetAccountListId(item.getAssetAccountRef() != null && item.getAssetAccountRef().getListID() != null ? item.getAssetAccountRef().getListID().getValue() : null);
                inventoryPart.setAssetAccountFullName(item.getAssetAccountRef() != null && item.getAssetAccountRef().getFullName() != null ? item.getAssetAccountRef().getFullName().getValue() : null);
                inventoryPart.setSalesDesc(item.getSalesDesc() != null ? item.getSalesDesc().getValue() : null);
                inventoryPart.setSalesPrice(item.getSalesPrice() != null ? item.getSalesPrice().getValue() : null);
                inventoryPart.setPurchaseDesc(item.getPurchaseDesc() != null ? item.getPurchaseDesc().getValue() : null);
                inventoryPart.setPurchaseCost(item.getPurchaseCost() != null ? item.getPurchaseCost().getValue() : null);
                inventoryPart.setReorderPoint(item.getReorderPoint() != null ? item.getReorderPoint().getValue() : null);
                inventoryPart.setQuantityOnHand(item.getQuantityOnHand() != null ? item.getQuantityOnHand().getValue() : null);
                inventoryPart.setAverageCost(item.getAverageCost() != null ? item.getAverageCost().getValue() : null);
                inventoryPart.setQuantityOnOrder(item.getQuantityOnOrder() != null ? item.getQuantityOnOrder().getValue() : null);
                inventoryPart.setQuantityOnSalesOrder(item.getQuantityOnSalesOrder() != null ? item.getQuantityOnSalesOrder().getValue() : null);
                inventoryPart.setItemType("Service");
                inventoryPart.setUpdatedAt(LocalDateTime.now());
                listOfInventoryParts.add(inventoryPart);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(item.getName() != null ? item.getName().getValue() : "Unknown item");
            }
        }
    }

    private String nodeToString(Element element) {
        try {
            javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(element);
            java.io.StringWriter writer = new java.io.StringWriter();
            javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(writer);
            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (Exception e) {
            return "";
        }
    }


    private InventoryRequestDTO.Add convertDTO(InventoryPart part) {
        InventoryRequestDTO.Add dto = new InventoryRequestDTO.Add();
        dto.setListId(part.getListId());
        dto.setTimeCreated(part.getTimeCreated());
        dto.setTimeModified(part.getTimeModified());
        dto.setEditSequence(part.getEditSequence());
        dto.setName(part.getName());
        dto.setFullName(part.getFullName());
        dto.setIsActive(part.getIsActive());
        dto.setSublevel(part.getSublevel());
        dto.setSalesTaxCodeListId(part.getSalesTaxCodeListId());
        dto.setSalesTaxCodeFullName(part.getSalesTaxCodeFullName());
        dto.setSalesOrPurchasePrice(part.getSalesOrPurchasePrice());
        dto.setSalesOrPurchaseAccountFullName(part.getSalesOrPurchaseAccountFullName());
        dto.setSalesOrPurchaseAccountListId(part.getSalesOrPurchaseAccountListId());
        dto.setDiscountRatePercentage(part.getDiscountRatePercentage());
        dto.setAccountListId(part.getAccountListId());
        dto.setAccountFullName(part.getAccountFullName());
        dto.setIncomeAccountListId(part.getIncomeAccountListId());
        dto.setIncomeAccountFullName(part.getIncomeAccountFullName());
        dto.setCogsAccountListId(part.getCogsAccountListId());
        dto.setCogsAccountFullName(part.getCogsAccountFullName());
        dto.setPrefVendorListId(part.getPrefVendorListId());
        dto.setPrefVendorFullName(part.getPrefVendorFullName());
        dto.setAssetAccountListId(part.getAssetAccountListId());
        dto.setAssetAccountFullName(part.getAssetAccountFullName());
        dto.setSalesDesc(part.getSalesDesc());
        dto.setSalesPrice(part.getSalesPrice());
        dto.setPurchaseDesc(part.getPurchaseDesc());
        dto.setPurchaseCost(part.getPurchaseCost());
        dto.setReorderPoint(part.getReorderPoint());
        dto.setQuantityOnHand(part.getQuantityOnHand());
        dto.setAverageCost(part.getAverageCost());
        dto.setQuantityOnOrder(part.getQuantityOnOrder());
        dto.setQuantityOnSalesOrder(part.getQuantityOnSalesOrder());
        dto.setItemDesc(part.getItemDesc());
        dto.setTaxRate(part.getTaxRate());
        dto.setTaxVendorListId(part.getTaxVendorListId());
        dto.setTaxVendorFullName(part.getTaxVendorFullName());
        dto.setSalePrice(part.getSalePrice());
        dto.setItemType(part.getItemType());
        return dto;
    }
}