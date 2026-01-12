package com.octal.fsm.dto.soap;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InventoryPartDTO {
    public QBXMLMsgsRs QBXMLMsgsRs;


    @Data
    public static class QBXMLMsgsRs {
        public ItemDiscountQueryRs ItemDiscountQueryRs;
        public ItemInventoryAssemblyQueryRs ItemInventoryAssemblyQueryRs;
        public ItemNonInventoryQueryRs ItemNonInventoryQueryRs;
        private ItemOtherChargeQueryRs ItemOtherChargeQueryRs;
        private ItemSalesTaxQueryRs ItemSalesTaxQueryRs;
        private ItemServiceQueryRs ItemServiceQueryRs;
        public ItemInventoryQueryRs ItemInventoryQueryRs;
    }

    @Data
    public static class ItemInventoryQueryRs {
        public List<ItemInventoryRet> ItemInventoryRet;
    }

    @Data
    public static class ItemDiscountQueryRs {
        public List<ItemInventoryRet> ItemDiscountRet;
    }

    @Data
    public static class ItemInventoryAssemblyQueryRs{
        public List<ItemInventoryRet> ItemInventoryAssemblyRet;
    }

    @Data
    public static class ItemNonInventoryQueryRs{
        public List<ItemInventoryRet> ItemNonInventoryRet;
    }

    @Data
    public static class ItemOtherChargeQueryRs{
        public List<ItemInventoryRet> ItemOtherChargeRet;
    }

    @Data
    public static class ItemSalesTaxQueryRs{
        public List<ItemInventoryRet> ItemSalesTaxRet;
    }

    @Data public static class ItemServiceQueryRs{
        public List<ItemInventoryRet> ItemServiceRet;
    }

    @Data
    public static class AssetAccountRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class AverageCost {
        public String value;
    }

    @Data
    public static class COGSAccountRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class EditSequence {
        public String value;
    }

    @Data
    public static class FullName {
        public String value;
    }

    @Data
    public static class IncomeAccountRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class IsActive {
        public String value;
    }

    @Data
    public static class ItemInventoryRet {
        public ListID ListID;
        public TimeCreated TimeCreated;
        public TimeModified TimeModified;
        public EditSequence EditSequence;
        public Name Name;
        public FullName FullName;
        public IsActive IsActive;
        public Sublevel Sublevel;
        public SalesTaxCodeRef SalesTaxCodeRef;
        public SalesDesc SalesDesc;
        public SalesPrice SalesPrice;
        public IncomeAccountRef IncomeAccountRef;
        public PurchaseDesc PurchaseDesc;
        public PurchaseCost PurchaseCost;
        public COGSAccountRef COGSAccountRef;
        public PrefVendorRef PrefVendorRef;
        public AssetAccountRef AssetAccountRef;
        public ReorderPoint ReorderPoint;
        public QuantityOnHand QuantityOnHand;
        public AverageCost AverageCost;
        public QuantityOnOrder QuantityOnOrder;
        public QuantityOnSalesOrder QuantityOnSalesOrder;
        public ParentRef ParentRef;
        public UnitOfMeasureSetRef UnitOfMeasureSetRef;
        public SalesOrPurchase SalesOrPurchase;
        public ItemDesc ItemDesc;
        public DiscountRatePercent DiscountRatePercent;
        public SalesOrPurchase.AccountRef AccountRef;
        public TaxRate TaxRate;
        public SalesOrPurchase.AccountRef TaxVendorRef;
    }

    @Data
    public static class TaxRate{
        public String value;
    }

    @Data
    public static class DiscountRatePercent{
        public String value;
    }

    @Data
    public static class ItemDesc{
        public String value;
    }

    @Data
    public static class SalesOrPurchase{
        public AccountRef AccountRef;
        public Price Price;

        @Data
        public static class AccountRef{
            public ListID ListID;
            public FullName FullName;
        }
        @Data
        public static class Price{
            public String value;
        }
    }

    @Data
    public static class ListID {
        public String value;
    }

    @Data
    public static class Name {
        public String value;
    }

    @Data
    public static class ParentRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class PrefVendorRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class PurchaseCost {
        public String value;
    }

    @Data
    public static class PurchaseDesc {
        public String value;
    }

    @Data
    public static class QuantityOnHand {
        public String value;
    }

    @Data
    public static class QuantityOnOrder {
        public String value;
    }

    @Data
    public static class QuantityOnSalesOrder {
        public String value;
    }

    @Data
    public static class ReorderPoint {
        public String value;
    }

    @Data
    public static class SalesDesc {
        public String value;
    }

    @Data
    public static class SalesPrice {
        public String value;
    }

    @Data
    public static class SalesTaxCodeRef {
        public ListID ListID;
        public FullName FullName;
    }

    @Data
    public static class Sublevel {
        public String value;
    }

    @Data
    public static class TimeCreated {
        public Date value;
    }

    @Data
    public static class TimeModified {
        public Date value;
    }

    @Data
    public static class UnitOfMeasureSetRef {
        public ListID ListID;
        public FullName FullName;
    }

}
