package com.octal.fsm.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "customer_type")
@Data
public class CustomerType extends AbstractPersistable {

    @Column(name = "list_id", nullable = false, unique = true)
    private String listId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "is_active",nullable = false)
    private String isActive;
}
