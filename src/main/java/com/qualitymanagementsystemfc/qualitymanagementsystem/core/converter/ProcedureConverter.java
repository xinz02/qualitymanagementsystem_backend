package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form.ProcedureInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ProcedureDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.PindaanDokumenVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureListInfo;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureVersionVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.CategoryService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.ModuleService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProcedureConverter {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleConverter moduleConverter;

    @Autowired
    private CategoryConverter categoryConverter;

    @Autowired
    private ProcedureTemplateConverter procedureTemplateConverter;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private PindaanDokumenConverter pindaanDokumenConverter;

    @Autowired
    private DateUtils dateUtils;

    public ProcedureDO convertDTOToDO(ProcedureDTO procedureDTO) {
        ProcedureDO procedureDO = new ProcedureDO();
        procedureDO.setProcedureNumber(procedureDTO.getProcedureNumber());
        procedureDO.setProcedureName(procedureDTO.getProcedureName());

        ModuleDO moduleDO = moduleService.findByModuleId(procedureDTO.getModuleId());
        if (moduleDO != null) {
            procedureDO.setModule(moduleDO);
        }

        CategoryDO categoryDO = categoryService.findByCategoryId(procedureDTO.getCategoryId());
        if (categoryDO != null) {
            procedureDO.setCategory(categoryDO);
        }

        procedureDO.setViewPrivilege(procedureDTO.getViewPrivilege());

//        if(!procedureDTO.getAssignedToIds().isEmpty()) {
//            List<UserDO> assignedUsers = userService.findAllByUserId(procedureDTO.getAssignedToIds());
//            procedureDO.setAssignTo(assignedUsers);
//        }
//
//        if(procedureDTO.getApproverId() != null && !procedureDTO.getApproverId().isBlank()) {
//            UserDO approver = userService.findByUserId(procedureDTO.getApproverId());
//            procedureDO.setApprover(approver);
//        }

//        procedureDO.setApproveStatus(ApproveStatus.PENDING.getCode());

        return procedureDO;
    }

    public ProcedureVO convertDOToVO(ProcedureDO procedureDO) {
        ProcedureVO procedureVO = new ProcedureVO();
        procedureVO.setProcedureId(procedureDO.getProcedureId());
        procedureVO.setProcedureNumber(procedureDO.getProcedureNumber());
        procedureVO.setProcedureName(procedureDO.getProcedureName());
        procedureVO.setModule(moduleConverter.convertDOToVO(procedureDO.getModule()));
        procedureVO.setCategory(categoryConverter.convertDOToVO(procedureDO.getCategory()));
        procedureVO.setViewPrivilege(procedureDO.getViewPrivilege());

//        List<User> userList = procedureDO
//                .getAssignTo().stream().map(userConverter::convertDOToModel).toList();
//        procedureVO.setAssignTo(userList);
//
//        if(procedureDO.getApprover() != null) {
//            procedureVO.setApprover(userConverter.convertDOToModel(procedureDO.getApprover()));
//        }

//        procedureVO.setApproveStatus(procedureDO.getApproveStatus());

//        ProcedureTemplateDO procedureTemplateDO = procedureDO.getProcedureTemplateData();

//        if(procedureTemplateDO != null ) {
//            procedureVO.setProcedureTemplateData(procedureTemplateConverter.convertDOToVO(procedureDO.getProcedureTemplateData()));
//        }

        if (procedureDO.getPindaanDokumenList() != null) {
            List<PindaanDokumenVO> pindaanDokumenVOList = procedureDO.getPindaanDokumenList().stream().map(pindaanDokumenConverter::convertModelToVO).toList();
            procedureVO.setPindaanDokumen(pindaanDokumenVOList);
        }

        if (procedureDO.getFileId() != null && !procedureDO.getFileId().isBlank()) {
            GridFSFile file = gridFsOperations.findOne(
                    Query.query(Criteria.where("_id").is(procedureDO.getFileId()))
            );

            if (file != null) {
                String fileName = file.getFilename();
                String fileType = file.getMetadata() != null ? file.getMetadata().getString("contentType") : null;
                long fileSize = file.getLength();

                procedureVO.setFileId(file.getObjectId().toString());
                procedureVO.setFileName(fileName);
                procedureVO.setFileType(fileType);
                procedureVO.setFileSize(fileSize);
                procedureVO.setFileDownloadUrl("/procedure/file/" + procedureDO.getFileId());
            }
        }

        return procedureVO;
    }

    public ProcedureDO convertExistingDOFromDTO(ProcedureDO existingProcedureDO, ProcedureDTO editProcedureDTO) {
        existingProcedureDO.setProcedureNumber(editProcedureDTO.getProcedureNumber());
        existingProcedureDO.setProcedureName(editProcedureDTO.getProcedureName());

        ModuleDO moduleDO = moduleService.findByModuleId(editProcedureDTO.getModuleId());
        if (moduleDO != null) {
            existingProcedureDO.setModule(moduleDO);
        }

        CategoryDO categoryDO = categoryService.findByCategoryId(editProcedureDTO.getCategoryId());
        if (categoryDO != null) {
            existingProcedureDO.setCategory(categoryDO);
        }

        existingProcedureDO.setViewPrivilege(editProcedureDTO.getViewPrivilege());

//        if(!editProcedureDTO.getAssignedToIds().isEmpty()) {
//            List<UserDO> assignedUsers = userService.findAllByUserId(editProcedureDTO.getAssignedToIds());
//            existingProcedureDO.setAssignTo(assignedUsers);
//        }
//
//        if(editProcedureDTO.getApproverId() != null && !editProcedureDTO.getApproverId().isBlank()) {
//            UserDO approver = userService.findByUserId(editProcedureDTO.getApproverId());
//            existingProcedureDO.setApprover(approver);
//        }

        return existingProcedureDO;

    }

    public ProcedureInfo convertDOToInfo(ProcedureDO procedureDO) {
        ProcedureInfo procedureInfo = new ProcedureInfo();
        procedureInfo.setProcedureId(procedureDO.getProcedureId());
        procedureInfo.setProcedureName(procedureDO.getProcedureName());

        return procedureInfo;
    }

    public ProcedureListInfo convertDOToListInfo(ProcedureDO procedureDO) {
        ProcedureListInfo procedureListInfo = new ProcedureListInfo();
        procedureListInfo.setProcedureId(procedureDO.getProcedureId());
        procedureListInfo.setProcedureNumber(procedureDO.getProcedureNumber());
        procedureListInfo.setProcedureName(procedureDO.getProcedureName());
        procedureListInfo.setModule(moduleConverter.convertDOToVO(procedureDO.getModule()));
        if(procedureDO.getPindaanDokumenList() != null) {
            procedureListInfo.setProcedureApproveStatus(procedureDO.getPindaanDokumenList().getLast().getApproveStatus());
            procedureListInfo.setVersion(procedureDO.getPindaanDokumenList().getLast().getVersi());
        } else {
            procedureListInfo.setProcedureApproveStatus("-");
            procedureListInfo.setVersion("-");
        }

        procedureListInfo.setCreatedAt(dateUtils.convertISODateToDate(procedureDO.getGmt_create()));
        procedureListInfo.setLastModified(dateUtils.convertISODateToDate(procedureDO.getGmt_modified()));


        return procedureListInfo;
    }

    public ProcedureVersionVO convertDOToVersionVO(ProcedureDO procedureDO) {
        ProcedureVersionVO procedureVersionVO = new ProcedureVersionVO();
        procedureVersionVO.setProcedureId(procedureDO.getProcedureId());
        procedureVersionVO.setProcedureNumber(procedureDO.getProcedureNumber());
        procedureVersionVO.setProcedureName(procedureDO.getProcedureName());
        procedureVersionVO.setModule(moduleConverter.convertDOToVO(procedureDO.getModule()));
        procedureVersionVO.setCategory(categoryConverter.convertDOToVO(procedureDO.getCategory()));
        procedureVersionVO.setViewPrivilege(procedureDO.getViewPrivilege());

        if (procedureDO.getPindaanDokumenList() != null && !procedureDO.getPindaanDokumenList().isEmpty()) {
            PindaanDokumen latestVersion = procedureDO.getPindaanDokumenList().stream()
                    .max(Comparator.comparingInt(p -> Integer.parseInt(p.getVersi())))
                    .orElseThrow(() -> new IllegalStateException("No pindaan versions found"));

            procedureVersionVO.setPindaanDokumen(pindaanDokumenConverter.convertModelToVO(latestVersion));
        }

        if (procedureDO.getFileId() != null && !procedureDO.getFileId().isBlank()) {
            GridFSFile file = gridFsOperations.findOne(
                    Query.query(Criteria.where("_id").is(procedureDO.getFileId()))
            );

            if (file != null) {
                String fileName = file.getFilename();
                String fileType = file.getMetadata() != null ? file.getMetadata().getString("contentType") : null;
                long fileSize = file.getLength();

                procedureVersionVO.setFileId(file.getObjectId().toString());
                procedureVersionVO.setFileName(fileName);
                procedureVersionVO.setFileType(fileType);
                procedureVersionVO.setFileSize(fileSize);
                procedureVersionVO.setFileDownloadUrl("/procedure/file/" + procedureDO.getFileId());
            }
        }

        return procedureVersionVO;
    }


}
