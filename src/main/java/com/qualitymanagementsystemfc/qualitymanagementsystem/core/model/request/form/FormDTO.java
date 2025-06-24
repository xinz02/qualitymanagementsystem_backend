package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.form;

import lombok.Data;

import java.util.List;

@Data
public class FormDTO {

    private String formNumber;

    private String formName;

    private String moduleId;

    private String categoryId;

    private List<String> viewPrivilege;

    private String personInChargeId;

    private String procedureId;

}
