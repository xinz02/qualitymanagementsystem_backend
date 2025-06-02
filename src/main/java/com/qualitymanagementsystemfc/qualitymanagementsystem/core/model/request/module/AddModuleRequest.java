package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.NewCategoryRequest;
import lombok.Data;

import java.util.List;

@Data
public class AddModuleRequest {

    private String moduleName;

    private List<String> viewPrivilege;

    private List<NewCategoryRequest> categories;

}
