package com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    STUDENT("STUDENT", "Students"),

    ACADEMIC_STAFF("ACADEMIC_STAFF", "Lecturers"),

    NON_ACADEMIC_STAFF("NON_ACADEMIC_STAFF", "Technicians"),

    ADMIN("ADMIN", "Admin"),

    SPK_MANAGER("SPK_MANAGER", "SPK Manager");

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

    //    public UserRole getByCode(String code) {
//
//        return STUDENT;
//    }
}
