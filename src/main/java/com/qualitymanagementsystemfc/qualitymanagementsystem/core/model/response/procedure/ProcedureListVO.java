package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import lombok.Data;

import java.util.List;

@Data
public class ProcedureListVO {

    private List<ProcedureListInfo> accessibleProcedures;

    private List<ProcedureListInfo> assignedProcedures;
}
