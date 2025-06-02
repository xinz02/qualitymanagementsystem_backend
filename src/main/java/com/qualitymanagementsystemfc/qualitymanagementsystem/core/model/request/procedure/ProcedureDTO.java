package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import lombok.Data;

import java.util.List;

@Data
public class ProcedureDTO {
    private String procedureNumber;

    private String procedureName;

    private String moduleId;

    private String categoryId;

    private List<String> viewPrivilege;

    private List<String> assignedToIds;

//    @DocumentReference(lazy = true)
//    private ProcedureTemplateDO templateData;
//
//    private byte procedureFile;

    private String approverId;

    private String approveStatus;
}
