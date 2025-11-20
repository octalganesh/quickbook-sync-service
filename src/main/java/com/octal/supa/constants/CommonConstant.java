package com.octal.supa.constants;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

public class CommonConstant {
    public static final String SHORTING = "Just Now";
    public static final String JUST_NOW = "Just Now";
    public static final String ORDER_EMAIL_TEMPLATE = "ORDER-EMAIL";
    public static final String EMAIL_PATTERN = "^(.+)@(\\S+)$";
    public static final String DUPLICATE_ENTRY = "Duplicate entry";

    public static final String STATUS = "isActive";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String CREATE_DATE = "createdAt";
    // Sorting fields for product
    public static final String ACTUAL_PRICE = "actualPrice";
    public static final String DEAL_PRICE = "dealPrice";
    public static final String STOCK_COUNT = "stockCount";


    // Sorting fields for Offer
    public static final String OFFER_TITLE = "offerTitle";
    public static final String OFFER_IN_PERCENTAGE = "offerInPercentage";
    public static final String OFFER_IN_PRICE = "offerInPrice";
    public static final String OFFER_START_DATE = "offerStartDate";
    public static final String OFFER_END_DATE = "offerEndDate";
    public static final String OFFER_TYPE = "offerType";
    public static final String NUMBER_OF_USAGE = "numberOfUsage";
    public static final String ORDER_PER_DAY = "ordersPerDay";

    // Sorting fields for loyalty rewards
    public static final String EVENT = "event";
    public static final String REWARD_POINT = "rewardPoint";
    public static final String PRICE_LIMIT = "priceLimit";
    public static final String SINGLE_USER_LIMIT = "singleUserLimit";

    // Sorting fields for loyalty rewards history
    public static final String LOYALTY_ORDER_ID = "orderId";
    public static final String LOYALTY_TRANSACTION_ID = "transactionId";
    public static final String LOYALTY_REASON = "reason";

    // Tax Sorting fields
    public static final String HSN_CODE = "hsnCode";
    public static final String TAX_PERCENTAGE = "taxPercentage";

    // Sorting fields for OrderDetails
    public static final String ORDER_STATUS = "orderStatus";
    public static final String ORDER_NO = "orderNo";
    public static final String ORDER_TOTAL_PRICE = "totalPrice";

    // Sorting fields for Email Template
    public static final String CMS_UPDATE_DATE = "updatedAt";
    public static final String CMS_TITLE = "title";
    public static final String CMS_SUBJECT = "emailSubject";

    // Sorting fields for Product Commission
    public static final String SETTLEMENT_STATUS = "settlementStatus";
    public static final String QUANTITY = "quantity";
    public static final String MERCHANT_ID = "merchantId";
    public static final String ACTOR_COMMISSION_AMT = "actorCommissionAmt";
    public static final String PRODUCT_ID = "productId";
    public static final String MERCHANT_EARNINGS = "merchantEarnings";
    // Sorting fields for FAQ
    public static final String FAQ_QUESTION = "question";

    public static final String USER_TYPE_MERCHANT = "merchant";

    public static final String USER_TYPE_ADMIN = "admin";

    public static final String USER_TYPE_SUB_ADMIN = "sub-admin";

    public static final String USER_TYPE_CUSTOMER = "customer";

    public static final String CANCELLATION_TYPE_PARTIAL = "PARTIAL";

    public static final String CANCELLATION_TYPE_FULL = "FULL";

    public static final String COMMISSION_PENDING = "PENDING";

    public static final String COMMISSION_CANCELLED = "CANCELLED";

    public static final String COMMISSION_SETTLED = "SETTLED";

    public static final String ORDER_EVENT_PLACE_ORDER = "PLACE_ORDER";

    public static final String ORDER_EVENT_CANCEL_ORDER = "CANCEL_ORDER";

    public static final String ORDER_NOTE_FLOW_TYPE = "POST";

    public static final String TRANSACTION_FEE = "transaction-fee";
    public static final String RETURN_FEE = "return-fee";
    public static final String ADMIN_COMMISSION = "admin-commission";
    public static final String CANCELLATION_FEE = "cancellation-fee";
    public static final String RETURN_DAYS = "return-days";
    public static final String WALLET_COMMISSION = "add-money-to-wallet-commission";
    public static final String ADMIN_EMAIL = "admin-email";
    public static final String ADMIN_ID = "admin-id";
    public static final String ADMIN_TRANSACTION_REMARK = "Added Admin Commission";
    public static final String REQUEST_MONEY_EXPIRATION_TIME = "request_money_expiry_time";
    public static final String CUSTOMR_WITHDRAW_MONEY_TO_BANK_LIMIT = "withdraw-money-to-bank-limit";
    public static final String CUSTOMER_ADD_MONEY_LIMIT = "customer-add-money-limit";
    public static final String CUSTOMER_TRASACTION_LIMIT = "customer-transaction-limit";
    public static final String BANK_TRANSACTION_MAX_AMOUNT = "bank-transaction-max-amount";
    public static final String BANK_TRANSACTION_MIN_AMOUNT = "bank-transaction-min-amount";
    public static final String BANK_TRANSACTION_LIMIT = "bank-transaction-limit";
    public static final String BANK_TRANSACTION_AMOUNT_LIMIT = "bank-transaction-amount-limit";
    public static final String BANK_ACCOUNT_COOL_DOWN_PERIOD = "bank-account-cool-down-period";
    public static final String LOYALTY_REWARD_CONVERSION_RATE = "loyalty-reward-conversion-rate";
    public static final String LOYALTY_REWARD_THRESHOLD_VALUE = "loyalty-reward-threshold-value";
    public static final String DEFAULT_PRIMARY_MERCHANT_PERMISSION = "defaultPrimaryMerchantPermissions";
    public static final String DEFAULT_SUB_MERCHANT_PERMISSION = "defaultSubMerchantPermissions";
    public static final String DEFAULT_ADMIN_PERMISSION = "defaultAdminPermissions";
    public static final String DEFAULT_SUB_ADMIN_PERMISSION = "defaultSubAdminPermissions";
    public static final String MONEY_REQUEST_ACCEPTED = "Money Request Accepted";
    public static final String EKYC_CLIENT_ID = "ekyc.client-id";
    public static final String EKYC_CLIENT_SECRET = "ekyc.client-secret";
    public static final String EKYC_BASE_ENDPOINT = "ekyc.base-endpoint";
    public static final String EKYC_VERIFICATION_ENDPOINT = "ekyc.verification-endpoint";
    public static final String EKYC_AADHAR_DOC_TYPE = "AADHAAR";
    public static final String EKYC_PAN_DOC_TYPE = "PAN";
    public static final String EKYC_GST_DOC_TYPE = "GST";
    public static final String EKYC_CIN_DOC_TYPE = "CIN";
    public static final String UTILITYBILLLINKKEY = "utility-bill-url";
    protected static final List<String> merchantSettings = Arrays.asList(RETURN_FEE, ADMIN_COMMISSION, CANCELLATION_FEE, RETURN_DAYS);
    protected static List<String> docTypes = Arrays.asList(CommonConstant.EKYC_AADHAR_DOC_TYPE, CommonConstant.EKYC_PAN_DOC_TYPE,
            CommonConstant.EKYC_GST_DOC_TYPE, CommonConstant.EKYC_CIN_DOC_TYPE);

    @Data
    public class SystemConfigurationConstants {
        public static final String PARAM_NAME = "paramName";
        public static final String PARAM_VALUE = "paramValue";
        public static final String PARAM_DESCRIPTION = "paramDescription";
        public static final String MODULE = "module";
        private SystemConfigurationConstants() {
            throw new IllegalStateException("Utility class");
        }

    }
}
