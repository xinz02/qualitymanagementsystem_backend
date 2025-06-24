package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.PindaanDokumenConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.enums.ApproveStatus;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.AddVersionRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.PindaanDokumenDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PindaanDokumenService {

    @Autowired
    private PindaanDokumenConverter pindaanDokumenConverter;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ProcedureTemplateService procedureTemplateService;

    @Autowired
    private EvidenceFileService evidenceFileService;

    public PindaanDokumen addPindaanDokumen(String pindaanDokumen) {

        try {
            PindaanDokumen pindaanDokumenDO;

            // pindaanDokumen is not null or empty
            // set value and pass back
            // else
            // initialize new pindaan and return
            if (pindaanDokumen != null && !pindaanDokumen.isBlank()) {

                ObjectMapper objectMapper = new ObjectMapper();

                PindaanDokumenDTO pindaanDokumenDTO = objectMapper.readValue(pindaanDokumen, PindaanDokumenDTO.class);

                if(pindaanDokumenDTO.getTarikh() == null || pindaanDokumenDTO.getTarikh().isBlank()) {
                    pindaanDokumenDTO.setTarikh(dateUtils.setTodayDateForTarikh());
                }

                pindaanDokumenDO = pindaanDokumenConverter.convertDTOToModel(pindaanDokumenDTO);

            } else {
                pindaanDokumenDO = new PindaanDokumen();
                pindaanDokumenDO.setVersi("1");
                pindaanDokumenDO.setTarikh(dateUtils.setTodayDateForTarikh());
                pindaanDokumenDO.setButiran("Keluaran Pertama");
                pindaanDokumenDO.setTarikh(dateUtils.setTodayDateForTarikh());
            }

            pindaanDokumenDO.setApproveStatus(ApproveStatus.PENDING.getCode());

            return pindaanDokumenDO;

        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }

    public PindaanDokumen editPindaanDokumen(String editPindaanDokumen, PindaanDokumen latestVersion) {
        try {
            if (editPindaanDokumen != null && !editPindaanDokumen.isBlank()) {
                ObjectMapper objectMapper = new ObjectMapper();

                PindaanDokumenDTO pindaanDokumenDTO = objectMapper.readValue(editPindaanDokumen, PindaanDokumenDTO.class);

                if (Integer.parseInt(pindaanDokumenDTO.getVersi()) != Integer.parseInt(latestVersion.getVersi())) {
                    throw new IllegalArgumentException("Only latest version of procedure is allowed to edit.");
                }


                if (ApproveStatus.APPROVE.getCode().equalsIgnoreCase(latestVersion.getApproveStatus())) {
                    // Latest version is approved — cannot edit this version
                    throw new IllegalStateException(
                            "Version " + latestVersion.getVersi() + " is already approved and cannot be edited. Please add a new version."
                    );
                }

                PindaanDokumen pindaanDokumen =  pindaanDokumenConverter.convertExistingDOFromDTO(pindaanDokumenDTO, latestVersion);
                pindaanDokumen.setApproveStatus(ApproveStatus.PENDING.getCode());
                return pindaanDokumen;

            } else {
                // no need to edit
                // return original version
                return latestVersion;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean deleteProcedureTemplateByVersion(String templateId) {

        return procedureTemplateService.deleteProcedureTemplate(templateId);
    }

    public PindaanDokumen createNewVersion(PindaanDokumen latestVersion, AddVersionRequest request) {
        PindaanDokumen newVersion = new PindaanDokumen();

        int newVersionNumber = Integer.parseInt(latestVersion.getVersi()) + 1;

        if(Integer.parseInt(request.getVersi()) != newVersionNumber ) {
            throw new IllegalArgumentException("Incorrect version");
        }

        newVersion.setVersi(request.getVersi());
        newVersion.setTarikh(dateUtils.setTodayDateForTarikh());
       newVersion.setApproveStatus(ApproveStatus.PENDING.getCode());

        if(request.getAssignedTo() != null && !request.getAssignedTo().isEmpty()) {
            List<UserDO> assignedUsers = userService.findAllByUserId(request.getAssignedTo());
            newVersion.setAssignTo(assignedUsers);
        }

//        newVersion.setButiran(request.getButiran());

        if(latestVersion.getProcedureTemplateData() != null) {
            ProcedureTemplateDO duplicateProcedureTemplateDO = procedureTemplateService.createNewTemplateVersion(latestVersion.getProcedureTemplateData());
            newVersion.setProcedureTemplateData(duplicateProcedureTemplateDO);
        }

        return newVersion;
    }


}
