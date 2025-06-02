package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ProcedureTemplateDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.ProcedureTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcedureTemplateConverter {

    @Autowired
    private FlowChartConverter flowChartConverter;

    public ProcedureTemplateDO convertDTOToDO(ProcedureTemplateDTO procedureTemplateDTO) {
        ProcedureTemplateDO procedureTemplateDO = new ProcedureTemplateDO();
        procedureTemplateDO.setNomborDokumen(procedureTemplateDTO.getNomborDokumen());
        procedureTemplateDO.setNamaDokumen(procedureTemplateDTO.getNamaDokumen());
        procedureTemplateDO.setPindaanDokumenList(procedureTemplateDTO.getPindaanDokumen());
        procedureTemplateDO.setTujuan(procedureTemplateDTO.getTujuan());
        procedureTemplateDO.setObjektif(procedureTemplateDTO.getObjektif());
        procedureTemplateDO.setSkop(procedureTemplateDTO.getSkop());
        procedureTemplateDO.setTerminologi(procedureTemplateDTO.getTerminologi());
        procedureTemplateDO.setSingkatan(procedureTemplateDTO.getSingkatan());
        procedureTemplateDO.setRujukan(procedureTemplateDTO.getRujukan());
        procedureTemplateDO.setProsedur(procedureTemplateDTO.getProsedur());
        procedureTemplateDO.setRekodDanSimpanan(procedureTemplateDTO.getRekodDanSimpanan());
        procedureTemplateDO.setLampiran(procedureTemplateDTO.getLampiran());

        return procedureTemplateDO;
    }

    public ProcedureTemplateVO convertDOToVO(ProcedureTemplateDO procedureTemplateDO) {
        ProcedureTemplateVO procedureTemplateVO = new ProcedureTemplateVO();
        procedureTemplateVO.setNamaDokumen(procedureTemplateDO.getNamaDokumen());
        procedureTemplateVO.setNomborDokumen(procedureTemplateDO.getNomborDokumen());
        procedureTemplateVO.setPindaanDokumen(procedureTemplateDO.getPindaanDokumenList());
        procedureTemplateVO.setCartaFungsi(flowChartConverter.convertDOToVO(procedureTemplateDO.getCartaFungsi()));
        procedureTemplateVO.setTujuan(procedureTemplateDO.getTujuan());
        procedureTemplateVO.setObjektif(procedureTemplateDO.getObjektif());
        procedureTemplateVO.setSkop(procedureTemplateDO.getSkop());
        procedureTemplateVO.setTerminologi(procedureTemplateDO.getTerminologi());
        procedureTemplateVO.setSingkatan(procedureTemplateDO.getSingkatan());
        procedureTemplateVO.setRujukan(procedureTemplateDO.getRujukan());
        procedureTemplateVO.setProsedur(procedureTemplateDO.getProsedur());
        procedureTemplateVO.setRekodDanSimpanan(procedureTemplateDO.getRekodDanSimpanan());
        procedureTemplateVO.setLampiran(procedureTemplateDO.getLampiran());

        return procedureTemplateVO;
    }

    public ProcedureTemplateDO convertExistingDOFromDTO(ProcedureTemplateDO existingProcedureTemplateDO, ProcedureTemplateDTO editProcedureTemplateDTO) {
        existingProcedureTemplateDO.setNamaDokumen(editProcedureTemplateDTO.getNamaDokumen());
        existingProcedureTemplateDO.setNomborDokumen(editProcedureTemplateDTO.getNomborDokumen());
        existingProcedureTemplateDO.setPindaanDokumenList(editProcedureTemplateDTO.getPindaanDokumen());
        existingProcedureTemplateDO.setTujuan(editProcedureTemplateDTO.getTujuan());
        existingProcedureTemplateDO.setObjektif(editProcedureTemplateDTO.getObjektif());
        existingProcedureTemplateDO.setSkop(editProcedureTemplateDTO.getSkop());
        existingProcedureTemplateDO.setTerminologi(editProcedureTemplateDTO.getTerminologi());
        existingProcedureTemplateDO.setSingkatan(editProcedureTemplateDTO.getSingkatan());
        existingProcedureTemplateDO.setRujukan(editProcedureTemplateDTO.getRujukan());
        existingProcedureTemplateDO.setProsedur(editProcedureTemplateDTO.getProsedur());
        existingProcedureTemplateDO.setRekodDanSimpanan(editProcedureTemplateDTO.getRekodDanSimpanan());
        existingProcedureTemplateDO.setLampiran(editProcedureTemplateDTO.getLampiran());

        return existingProcedureTemplateDO;
    }
}
