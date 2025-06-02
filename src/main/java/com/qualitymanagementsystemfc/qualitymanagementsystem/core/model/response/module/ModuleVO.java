package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import lombok.Data;

import java.util.List;

@Data
public class ModuleVO {
    //    private Map<Module, List<Category>> modulesWithCategories;
    private String moduleId;

    private String moduleName;

    private List<String> viewPrivilege;

    private List<CategoryVO> categories;
}
