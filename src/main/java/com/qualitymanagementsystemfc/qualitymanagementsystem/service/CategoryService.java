package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.CategoryConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.DeleteCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.EditCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.category.NewCategoryRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.CategoryRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ModuleRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ProcedureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryConverter categoryConverter;

    @Autowired
    private ProcedureRepository procedureRepository;

    public List<CategoryDO> getAllCategory() {
        return categoryRepository.findAll();
    }

    public List<CategoryDO> getAllCategoriesByModuleId(String moduleId) {
        return categoryRepository.findByModuleId(moduleId);
    }

    @Transactional
    public List<CategoryDO> addNewCategory(List<NewCategoryRequest> newCategoryList, String moduleId) {
        ModuleDO module = moduleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module not found."));

        // Get existing category names under this module
        Set<String> existingCategoryNames = module.getCategories()
                .stream()
                .map(CategoryDO::getCategoryName)
                .collect(Collectors.toSet());

        // Check if any new category name already exists
        boolean existsDuplicate = newCategoryList.stream()
                .anyMatch(newCat -> existingCategoryNames.contains(newCat.getCategoryName()));

        if (existsDuplicate) {
            throw new IllegalArgumentException("Category with the same name already exists under this module.");
        }

        List<CategoryDO> savedCategoryDOList = newCategoryList.stream().map(c -> {
            CategoryDO categoryDO = categoryConverter.buildCategoryDO(c.getCategoryName(), moduleId);
            categoryDO.setGmt_create(new Date());
            categoryDO.setGmt_modified(new Date());

            return categoryDO;
        }).toList();

        return categoryRepository.saveAll(savedCategoryDOList);
    }

    @Transactional
    public void editCategory(List<EditCategoryRequest> editCategoryList) {

        List<CategoryDO> savedCategoryDOList = editCategoryList.stream().map(c -> {
            CategoryDO categoryDO = categoryRepository.findById(c.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category " + c.getCategoryId() + " not found."));
            categoryDO.setGmt_modified(new Date());
            categoryDO.setCategoryName(c.getCategoryName());
            return categoryDO;
        }).toList();

        categoryRepository.saveAll(savedCategoryDOList);
    }

//    @Transactional
//    public void deleteCategory(List<DeleteCategoryRequest> deleteCategoryList) {
//
//        List<String> ids = deleteCategoryList.stream()
//                .map(DeleteCategoryRequest::getCategoryId)
//                .toList();
//
//        List<CategoryDO> categories = categoryRepository.findAllById(ids);
//
//        if (categories.size() != ids.size()) {
//            throw new IllegalArgumentException("Some categories do not exist in database.");
//        }
//
//        categoryRepository.deleteAll(categories);
//    }

    @Transactional
    public List<String> deleteCategory(List<DeleteCategoryRequest> deleteCategoryList) {
        List<String> deletedIds = new ArrayList<>();

        for (DeleteCategoryRequest d : deleteCategoryList) {
            if (!categoryRepository.existsById(d.getCategoryId())) {
                throw new IllegalArgumentException("Category " + d.getCategoryId() + " does not exist.");
            }

            boolean existsProceduresUnderCategory = procedureRepository.existsByCategory_CategoryId(d.getCategoryId());

            if(existsProceduresUnderCategory) {
                throw new IllegalArgumentException("Unable to delete because exists procedure under selected category.");
            }

            categoryRepository.deleteById(d.getCategoryId());
            deletedIds.add(d.getCategoryId());
        }

        return deletedIds;
    }

    public CategoryDO findByCategoryId(String categoryId) {

        return categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("Category not exist."));
    }

    @Transactional
    public List<String> deleteCategoryDOList(List<CategoryDO> deleteCategoryList) {
        List<String> deletedIds = new ArrayList<>();

        for (CategoryDO d : deleteCategoryList) {
            if (!categoryRepository.existsById(d.getCategoryId())) {
                throw new IllegalArgumentException("Category " + d.getCategoryId() + " does not exist.");
            }

            boolean existsProceduresUnderCategory = procedureRepository.existsByCategory_CategoryId(d.getCategoryId());

            if(existsProceduresUnderCategory) {
                throw new IllegalArgumentException("Unable to delete because exists procedure under selected category.");
            }

            categoryRepository.deleteById(d.getCategoryId());
            deletedIds.add(d.getCategoryId());
        }

        return deletedIds;
    }


}
