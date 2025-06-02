package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.DeleteCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.EditCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.NewCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import lombok.Data;

import java.util.List;

@Data
public class EditModuleRequest {

    private String moduleId;

    private String moduleName;

    private List<String> viewPrivilege;

    private List<NewCategoryRequest> categories;

    private List<EditCategoryRequest> categoriesToEdit;

    private List<DeleteCategoryRequest> categoriesToDelete;

}
