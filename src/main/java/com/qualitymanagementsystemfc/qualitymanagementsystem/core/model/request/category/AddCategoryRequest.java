package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import lombok.Data;

@Data
public class AddCategoryRequest {
    private CategoryDO category;
}
