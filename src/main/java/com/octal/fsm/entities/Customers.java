package com.octal.fsm.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
@Data
public class Customers extends AbstractPersistable {

    @Column(name = "list_id", nullable = false, unique = true)
    private String listId;

    @Column(name = "name")
    private String name;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String timeCreated;
    private String timeModified;
    private String editSequence;
    private String sublevel;
    private String balance;
    private String totalBalance;
    private String jobStatus;
    private String preferredDeliveryMethod;

    private String customerTypeId;
    private String customerTypeName;
    private String email;

}
