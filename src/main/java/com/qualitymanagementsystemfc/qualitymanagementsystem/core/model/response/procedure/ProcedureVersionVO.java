package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import lombok.Data;

import java.util.List;

@Data
public class ProcedureVersionVO {
    private String procedureId;

    private String procedureNumber;

    private String procedureName;

    private ModuleVO module;

    private CategoryVO category;

    private List<String> viewPrivilege;

    private PindaanDokumenVO pindaanDokumen;

    private String fileId;

    private String fileName;

    private String fileType;

    private String fileDownloadUrl;

    private long fileSize;

//    private User approver;
//
//    private String approveStatus;

//    private Date gmt_create;
//
//    private Date gmt_modified;
}
