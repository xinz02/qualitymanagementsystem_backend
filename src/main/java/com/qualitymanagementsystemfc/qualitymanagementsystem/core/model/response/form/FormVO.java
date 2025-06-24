package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.form;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.ProcedureInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import lombok.Data;

import java.util.List;

@Data
public class FormVO {
    private String formId;

    private String formNumber;

    private String formName;

    private ModuleVO module;

    private CategoryVO category;

    private List<String> viewPrivilege;

    private User personInCharge;

    private ProcedureInfo relatedProcedure;

    private String fileName;

    private String fileType;

    private String fileDownloadUrl;

    private long fileSize;

}
