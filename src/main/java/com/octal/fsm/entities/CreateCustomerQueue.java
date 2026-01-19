package com.octal.fsm.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "create_customer_queue")
@Data
public class CreateCustomerQueue extends AbstractPersistable {

    @Column(name = "sync_status", nullable = false)
    private String syncStatus;

    @Lob
    @Column(name = "create_customer_json_response")
    private String createCustomerJsonResponse;

    @Lob
    @Column(name = "create_customer_xml_response")
    private String createCustomerXmlResponse;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "status_severity")
    private String statusSeverity;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "active_token")
    private String activeToken;

    @Column(name = "quick_book_customer_id")
    private String quickBookCustomerId;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId; // customer table record Id from admin service

    @Column(name = "email")
    private String email;

    @Column(name = "secondary_email")
    private String secondaryEmail;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "alternative_mobile_1")
    private String alterNativeMobile1;

    @Column(name = "alternative_mobile_2")
    private String alterNativeMobile2;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private String gender;

    @Column(name = "customer_type_id")
    private String customerTypeId;

    @Column(name = "customer_type_name")
    private String customerTypeName;

    @Column(name = "customer_uuid", nullable = false, unique = true)
    private String customerUuid;


}
