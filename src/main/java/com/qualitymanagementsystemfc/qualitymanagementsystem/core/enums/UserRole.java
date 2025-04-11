package com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    STUDENT("Student", "Students"),

    ACADEMIC_STAFF("Academic Staff", "Lecturers"),

    NON_ACADEMIC_STAFF("Non-Academic Staff", "Technicians"),

    SPK_MANAGER("SPK Manager", "SPK Manager");

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
