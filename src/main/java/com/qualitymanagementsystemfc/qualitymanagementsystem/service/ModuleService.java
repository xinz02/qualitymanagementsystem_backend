package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.CategoryConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.ModuleConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.AddModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.EditModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.CategoryRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private ModuleConverter moduleConverter;

    @Autowired
    private CategoryConverter categoryConverter;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

//    public Module addModule(Module module) {
//        Module existedModule = moduleRepository.findByModuleName(module.getModuleName());
//
//        if (existedModule != null) {
//            throw new IllegalArgumentException("Module with the same name already exists.");
//        }
//
//        module.setGmt_create(new Date());
//        module.setGmt_modified(new Date());
//        return moduleRepository.save(module);
//    }

//    public GetModuleResponse getAllModules(String role) {
//        GetModuleResponse response = new GetModuleResponse();
//
//        List<Module> allModules = moduleRepository.findByViewPrivilegeContaining(role);
//
//        List<Category> allCategories = allModules.stream().flatMap(module -> categoryService.getAllCategoriesByModuleId(module.getModuleId()).stream()).toList();
//
//        Map<Module, List<Category>> moduleWithRespectiveCategories = new HashMap<>();
//
//        for(Module module : allModules) {
//            moduleWithRespectiveCategories.computeIfAbsent(module, k -> allCategories.stream().filter(category -> category.getModuleId().equals(module.getModuleId())).toList());
//        }
//
//        response.setModulesWithCategories(moduleWithRespectiveCategories);
//
//        return response;
//    }

    public List<ModuleVO> getAllModules() {
        ModuleVO moduleVO = new ModuleVO();

        List<ModuleDO> allModules = moduleRepository.findAll();

        return allModules.stream().map(m -> moduleConverter.convertDOToVO(m)).toList();
    }

    public List<ModuleVO> getAllModulesByRole(String role) {
        ModuleVO moduleVO = new ModuleVO();

        List<ModuleDO> allModules = moduleRepository.findByViewPrivilegeContaining(role);

        return allModules.stream().map(m -> moduleConverter.convertDOToVO(m)).toList();
    }

    @Transactional
    public ModuleVO addModule(AddModuleRequest module) {
        boolean moduleExisted = moduleRepository.existsByModuleName(module.getModuleName());

        if (moduleExisted) {
            throw new IllegalArgumentException("Module with the same name already exists.");
        }

        ModuleDO newModule = moduleConverter.convertDTOToDO(module);

        newModule.setGmt_create(new Date());
        newModule.setGmt_modified(new Date());
        ModuleDO savedModule = moduleRepository.save(newModule);

        if(module.getCategories() != null) {
            List<CategoryDO> savedCategoryDOList = categoryService.addNewCategory(module.getCategories(), savedModule.getModuleId());
            savedModule.setCategories(savedCategoryDOList);
//            savedModule.getCategories().addAll(savedCategoryDOList);
            moduleRepository.save(savedModule);
        }

        return moduleConverter.convertDOToVO(savedModule);
    }

    @Transactional
    public ModuleVO editModule(EditModuleRequest module) {

        ModuleDO existedModule = moduleRepository.findById(module.getModuleId()).orElseThrow(() -> new IllegalArgumentException("Module not exist."));

        existedModule.setModuleName(module.getModuleName());
        existedModule.setViewPrivilege(module.getViewPrivilege());
        existedModule.setGmt_modified(new Date());

        if(module.getCategories() != null) {
            List<CategoryDO> savedCategoryDOList = categoryService.addNewCategory(module.getCategories(), module.getModuleId());
            if (existedModule.getCategories() == null) {
                existedModule.setCategories(new ArrayList<>());
            }
            existedModule.getCategories().addAll(savedCategoryDOList);
        }

        if(module.getCategoriesToEdit() != null) {
//            List<CategoryDO> editedCategoryDOList = categoryService.editCategory(module.getCategoriesToEdit());
            categoryService.editCategory(module.getCategoriesToEdit());
        }

        if (module.getCategoriesToDelete() != null) {
            List<String> deletedCategoryIds = categoryService.deleteCategory(module.getCategoriesToDelete());

            if (existedModule.getCategories() != null) {
                existedModule.getCategories().removeIf(category -> deletedCategoryIds.contains(category.getCategoryId()));
            }
        }

        moduleRepository.save(existedModule);


        return moduleConverter.convertDOToVO(existedModule);

    }

    @Transactional
    public boolean deleteModule(String moduleId) {
        ModuleDO moduleDO = moduleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module does not exists."));

        if(moduleDO.getCategories().isEmpty()) {
            moduleRepository.deleteById(moduleId);
            return true;
        }

        return false;
    }

    public ModuleDO findByModuleId(String moduleId) {
        return moduleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module does not exists."));
    }

}
