package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.FormDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.ProcedureInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.form.FormDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.form.FormVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.CategoryService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ModuleService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ProcedureService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;

@Component
public class FormConverter {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProcedureService procedureService;

    @Autowired
    private ModuleConverter moduleConverter;

    @Autowired
    private CategoryConverter categoryConverter;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ProcedureConverter procedureConverter;

    @Autowired
    private GridFsOperations gridFsOperations;

    public FormDO convertDTOToDO(FormDTO formDTO) {
        FormDO formDO = new FormDO();
        formDO.setFormNumber(formDTO.getFormNumber());
        formDO.setFormName(formDTO.getFormName());

        ModuleDO moduleDO = moduleService.findByModuleId(formDTO.getModuleId());
        if (moduleDO != null) {
            formDO.setModule(moduleDO);
        }

        CategoryDO categoryDO = categoryService.findByCategoryId(formDTO.getCategoryId());
        if (categoryDO != null) {
            formDO.setCategory(categoryDO);
        }

        UserDO personInCharge = userService.findByUserId(formDTO.getPersonInChargeId());
        formDO.setPersonInCharge(personInCharge);

        if (formDTO.getProcedureId() != null && !formDTO.getProcedureId().isBlank() && !formDTO.getProcedureId().equals("NO_PROCEDURE")) {
            ProcedureDO procedureDO = procedureService.getProcedureDOById(formDTO.getProcedureId());
            formDO.setRelatedProcedure(procedureDO);
        }

        formDO.setViewPrivilege(formDTO.getViewPrivilege());

        return formDO;
    }

    public FormVO convertDOToVO(FormDO formDO) {
        FormVO formVO = new FormVO();
        formVO.setFormId(formDO.getFormId());
        formVO.setFormNumber(formDO.getFormNumber());
        formVO.setFormName(formDO.getFormName());
        formVO.setModule(moduleConverter.convertDOToVO(formDO.getModule()));
        formVO.setCategory(categoryConverter.convertDOToVO(formDO.getCategory()));
        formVO.setViewPrivilege(formDO.getViewPrivilege());

        GridFSFile file = gridFsOperations.findOne(
                Query.query(Criteria.where("_id").is(formDO.getFileId()))
        );

        if (file != null) {
            String fileName = file.getFilename();
            String fileType = file.getMetadata() != null ? file.getMetadata().getString("contentType") : null;
            long fileSize = file.getLength();

            formVO.setFileName(fileName);
            formVO.setFileType(fileType);
            formVO.setFileSize(fileSize);
            formVO.setFileDownloadUrl("/procedure/file/" + formDO.getFileId());
        }

        User personInCharge = userConverter.convertDOToModel(formDO.getPersonInCharge());
        formVO.setPersonInCharge(personInCharge);

        if (formDO.getRelatedProcedure() != null) {
            ProcedureInfo procedureInfo = procedureConverter.convertDOToInfo(formDO.getRelatedProcedure());
            formVO.setRelatedProcedure(procedureInfo);
        }

        return formVO;
    }

    public FormDO convertExistingDOFromDTO(FormDO existingDO, FormDTO editFormDTO) {
        existingDO.setFormNumber(editFormDTO.getFormNumber());
        existingDO.setFormName(editFormDTO.getFormName());

        ModuleDO moduleDO = moduleService.findByModuleId(editFormDTO.getModuleId());
        if(moduleDO != null) {
            existingDO.setModule(moduleDO);
        }

        CategoryDO categoryDO = categoryService.findByCategoryId(editFormDTO.getCategoryId());
        if(categoryDO != null) {
            existingDO.setCategory(categoryDO);
        }

        existingDO.setViewPrivilege(editFormDTO.getViewPrivilege());

        UserDO personInCharge = userService.findByUserId(editFormDTO.getPersonInChargeId());
        if(personInCharge != null) {
            existingDO.setPersonInCharge(personInCharge);
        }

        if(!editFormDTO.getProcedureId().equals("NO_PROCEDURE")) {
            ProcedureDO relatedProcedure = procedureService.getProcedureDOById(editFormDTO.getProcedureId());
            existingDO.setRelatedProcedure(relatedProcedure);
        } else {
            existingDO.setRelatedProcedure(null);
        }

        return existingDO;

    }

}
