package com.qualitymanagementsystemfc.qualitymanagementsystem.repository;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryRepository extends MongoRepository<CategoryDO, String> {

    public CategoryDO findByCategoryName(String categoryName);

    public List<CategoryDO> findByModuleId(String moduleId);

    public CategoryDO findByCategoryNameAndModuleId(String categoryName, String moduleId);
}
