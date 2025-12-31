package com.octal.supa.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "create_invoice_queue")
@Data
public class CreateInvoiceQueue extends AbstractPersistable {

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "list_id")
    private String listId;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "customer_list_id", nullable = false)
    private String customerListId;

    @Column(name = "customer_full_name", nullable = false)
    private String customerFullName;

    @Column(name = "sync_status", nullable = false)
    private String syncStatus;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Lob
    @Column(name = "create_invoice_json_response")
    private String createInvoiceJsonResponse;

    @Lob
    @Column(name = "create_invoice_xml_response")
    private String createInvoiceXmlResponse;

    @Column(name = "status_code")
    private String statusCode;

    @Column(name = "status_severity")
    private String statusSeverity;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "active_token")
    private String activeToken;

}
