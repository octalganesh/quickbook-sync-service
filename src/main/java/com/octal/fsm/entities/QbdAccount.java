package com.octal.fsm.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "qbd_account", uniqueConstraints = {
        @UniqueConstraint(columnNames = "list_id")
})
@Data
public class QbdAccount extends AbstractPersistable{

    @Column(name = "list_id", nullable = false)
    private String listId;

    private String name;
    private String fullName;
    private String accountType;
    private String accountNumber;
    private Boolean active;
    private BigDecimal balance;
    private BigDecimal totalBalance;
}
