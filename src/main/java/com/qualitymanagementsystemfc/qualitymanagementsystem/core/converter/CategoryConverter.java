package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public CategoryDO buildCategoryDO (String categoryName, String moduleId) {
        CategoryDO categoryDO = new CategoryDO();
        categoryDO.setCategoryName(categoryName);

        if(moduleId != null) {
            categoryDO.setModuleId(moduleId);
        }

        return categoryDO;
    }

    public CategoryVO convertDOToVO (CategoryDO categoryDO) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setCategoryId(categoryDO.getCategoryId());
        categoryVO.setCategoryName(categoryDO.getCategoryName());
        return categoryVO;
    }

}
