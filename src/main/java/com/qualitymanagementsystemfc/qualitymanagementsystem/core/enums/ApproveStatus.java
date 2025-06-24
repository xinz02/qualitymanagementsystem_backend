package com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ApproveStatus {

    APPROVE("APPROVE", "Approved"),

    PENDING("PENDING", "Pending"),

    REJECT("REJECT", "Reject");

    private String code;

    private String description;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
