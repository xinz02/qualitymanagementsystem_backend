package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import lombok.Data;

@Data
public class ApproveProcedureDTO {

    private String approverId;

    private String status;

    private String description;

    private String version;

}
