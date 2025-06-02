package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.module.AddModuleRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.category.CategoryVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.module.ModuleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModuleConverter {

    @Autowired
    private CategoryConverter categoryConverter;

    public ModuleDO convertDTOToDO(AddModuleRequest moduleDTO) {
        ModuleDO moduleDO = new ModuleDO();
        moduleDO.setModuleName(moduleDTO.getModuleName());
        moduleDO.setViewPrivilege(moduleDTO.getViewPrivilege());
        return moduleDO;
    }

    public ModuleVO convertDOToVO(ModuleDO moduleDO) {
        ModuleVO moduleVO = new ModuleVO();
        moduleVO.setModuleId(moduleDO.getModuleId());
        moduleVO.setModuleName(moduleDO.getModuleName());
        moduleVO.setViewPrivilege(moduleDO.getViewPrivilege());

        if (moduleDO.getCategories() == null) {
            moduleDO.setCategories(new ArrayList<>());
        } else {
            List<CategoryVO> categoryVOList = moduleDO.getCategories().stream().map(c -> categoryConverter.convertDOToVO(c)).toList();
            moduleVO.setCategories(categoryVOList);
        }
        return moduleVO;
    }
}
