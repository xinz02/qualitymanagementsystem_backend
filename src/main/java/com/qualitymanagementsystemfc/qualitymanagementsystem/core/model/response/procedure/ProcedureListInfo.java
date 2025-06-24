package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import lombok.Data;

@Data
public class ProcedureListInfo {
    private String procedureId;

    private String procedureNumber;

    private String procedureName;

    private ModuleVO module;

    private String procedureApproveStatus;

    private String version;

    private String createdAt;

    private String lastModified;

}
