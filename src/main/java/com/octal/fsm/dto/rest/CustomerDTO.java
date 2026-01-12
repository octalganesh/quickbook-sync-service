package com.octal.fsm.dto.rest;

import lombok.Data;

@Data
public class CustomerDTO {

    private String listId;
    private String name;
    private String fullName;
    private String isActive;

    private String timeCreated;
    private String timeModified;
    private String editSequence;
    private String sublevel;

    private String balance;
    private String totalBalance;
    private String jobStatus;
    private String preferredDeliveryMethod;
    private String mobileNo;
    private String customerTypeName;
    private String email;

}
