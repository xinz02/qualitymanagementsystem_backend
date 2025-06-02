package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.Data;

import java.util.List;

@Data
public class ProcedureVO {

    private String procedureId;

    private String procedureNumber;

    private String procedureName;

    private ModuleVO module;

    private CategoryVO category;

    private List<String> viewPrivilege;

    private List<User> assignTo;

    private ProcedureTemplateVO procedureTemplateData;

    //    private byte procedureFile;
    private String fileName;

    private String fileType;

    private String fileDownloadUrl;

    private long fileSize;

    private User approver;

    private String approveStatus;

//    private Date gmt_create;
//
//    private Date gmt_modified;
}
