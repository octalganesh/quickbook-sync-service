package com.octal.supa.entities;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "create_customer_queue")
@Data
public class CreateCustomerQueue extends AbstractPersistable {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "customer_id", nullable = false, unique = true)
    private String customerId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "secondary_email", nullable = false)
    private String secondaryEmail;

    @Column(name = "customer_type", nullable = false)
    private String customer_type;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "alternative_mobile_1", nullable = false)
    private String alterNativeMobile1;

    @Column(name = "alternative_mobile_2", nullable = false)
    private String alterNativeMobile2;

    @Column(name = "address", nullable = false)
    private String address;

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

    @Column(name = "quick_book_customer_id", unique = true, nullable = false)
    private String quickBookCustomerId;

}
