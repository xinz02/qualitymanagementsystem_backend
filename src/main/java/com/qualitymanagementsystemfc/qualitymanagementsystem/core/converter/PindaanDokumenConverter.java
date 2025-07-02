package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.PindaanDokumenDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.PindaanDokumenVO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PindaanDokumenConverter {

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private ProcedureTemplateConverter procedureTemplateConverter;

    public PindaanDokumen convertDTOToModel(PindaanDokumenDTO pindaanDokumenDTO) {
        PindaanDokumen pindaanDokumen = new PindaanDokumen();
        pindaanDokumen.setVersi(pindaanDokumenDTO.getVersi());
        pindaanDokumen.setTarikh(pindaanDokumenDTO.getTarikh());
        pindaanDokumen.setButiran(pindaanDokumenDTO.getButiran());
        pindaanDokumen.setDeskripsiPerubahan(pindaanDokumenDTO.getDeskripsiPerubahan());

        if (pindaanDokumenDTO.getDisediakan() != null && !pindaanDokumenDTO.getDisediakan().isEmpty()) {
            List<UserDO> disediakanUsers = userService.findAllByUserId(pindaanDokumenDTO.getDisediakan());
            pindaanDokumen.setDisediakan(disediakanUsers);
        }

        if (pindaanDokumenDTO.getDiluluskan() != null && !pindaanDokumenDTO.getDiluluskan().isEmpty()) {
            UserDO diluluskanUser = userService.findByUserId(pindaanDokumenDTO.getDiluluskan());
            pindaanDokumen.setDiluluskan(diluluskanUser);
        }

        if (pindaanDokumenDTO.getAssignedTo() != null && !pindaanDokumenDTO.getAssignedTo().isEmpty()) {
            List<UserDO> assignedUsers = userService.findAllByUserId(pindaanDokumenDTO.getAssignedTo());
            pindaanDokumen.setAssignTo(assignedUsers);
        }

        return pindaanDokumen;
    }

    public PindaanDokumenVO convertModelToVO(PindaanDokumen pindaanDokumen) {
        PindaanDokumenVO pindaanDokumenVO = new PindaanDokumenVO();
        pindaanDokumenVO.setVersi(pindaanDokumen.getVersi());
        pindaanDokumenVO.setTarikh(pindaanDokumen.getTarikh());
        pindaanDokumenVO.setButiran(pindaanDokumen.getButiran());
        pindaanDokumenVO.setDeskripsiPerubahan(pindaanDokumen.getDeskripsiPerubahan());

        if (pindaanDokumen.getDisediakan() != null && !pindaanDokumen.getDisediakan().isEmpty()) {
            List<User> disediakanUsers = pindaanDokumen.getDisediakan().stream().map(userConverter::convertDOToModel).toList();
            pindaanDokumenVO.setDisediakan(disediakanUsers);

        }

        if (pindaanDokumen.getDiluluskan() != null) {
            pindaanDokumenVO.setDiluluskan(userConverter.convertDOToModel(pindaanDokumen.getDiluluskan()));
        }

        if (pindaanDokumen.getAssignTo() != null && !pindaanDokumen.getAssignTo().isEmpty()) {
            List<User> assignedUsers = pindaanDokumen.getAssignTo().stream().map(userConverter::convertDOToModel).toList();
            pindaanDokumenVO.setAssignTo(assignedUsers);

        }

        if (pindaanDokumen.getApprover() != null) {
            pindaanDokumenVO.setApprover(userConverter.convertDOToModel(pindaanDokumen.getApprover()));
        }

        pindaanDokumenVO.setApproveStatus(pindaanDokumen.getApproveStatus());
        pindaanDokumenVO.setDescription(pindaanDokumen.getDescription());

        if (pindaanDokumen.getProcedureTemplateData() != null) {
            pindaanDokumenVO.setProcedureTemplateData(procedureTemplateConverter.convertDOToVO(pindaanDokumen.getProcedureTemplateData()));
        }

        return pindaanDokumenVO;
    }

    public PindaanDokumen convertExistingDOFromDTO(PindaanDokumenDTO editDTO, PindaanDokumen latestVersion) {
        latestVersion.setTarikh(editDTO.getTarikh());
        latestVersion.setButiran(editDTO.getButiran());
        latestVersion.setDeskripsiPerubahan(editDTO.getDeskripsiPerubahan());

        if (editDTO.getDisediakan() != null && !editDTO.getDisediakan().isEmpty()) {
            List<UserDO> disediakanUsers = userService.findAllByUserId(editDTO.getDisediakan());
            latestVersion.setDisediakan(disediakanUsers);
        } else {
            latestVersion.setDisediakan(null);
        }

        if (editDTO.getDiluluskan() != null && !editDTO.getDiluluskan().isEmpty()) {
            UserDO diluluskanUser = userService.findByUserId(editDTO.getDiluluskan());
            latestVersion.setDiluluskan(diluluskanUser);
        } else {
            latestVersion.setDiluluskan(null);
        }

        if (editDTO.getAssignedTo() != null && !editDTO.getAssignedTo().isEmpty()) {
            List<UserDO> assignedUsers = userService.findAllByUserId(editDTO.getAssignedTo());
            latestVersion.setAssignTo(assignedUsers);
        } else {
            latestVersion.setAssignTo(new ArrayList<>());
        }

        return latestVersion;
    }
}
