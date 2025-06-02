package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.ProcedureTemplateConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.ProcedureTemplateDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.ProcedureTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ProcedureTemplateService {

    @Autowired
    private ProcedureTemplateRepository procedureTemplateRepository;

    @Autowired
    private ProcedureTemplateConverter procedureTemplateConverter;

    @Autowired
    private FlowChartService flowChartService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Add new procedure template
     * @param procedureTemplateData
     * @return
     */
    @Transactional
    public ProcedureTemplateDO addProcedureTemplate(String procedureTemplateData) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ProcedureTemplateDTO procedureTemplateDTO = objectMapper.readValue(procedureTemplateData, ProcedureTemplateDTO.class);

            ProcedureTemplateDO procedureTemplateDO = procedureTemplateConverter.convertDTOToDO(procedureTemplateDTO);
            procedureTemplateDO.setGmt_create(new Date());
            procedureTemplateDO.setGmt_modified(new Date());

            if(procedureTemplateDTO.getCartaFungsi() != null) {
                FlowChartDO flowChartDO = flowChartService.addFlowChart(procedureTemplateDTO.getCartaFungsi());
                procedureTemplateDO.setCartaFungsi(flowChartDO);
            }

            return procedureTemplateRepository.save(procedureTemplateDO);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);

        }

    }

    /**
     * Edit existing procedure template
     * @param templateId
     * @param procedureTemplateData
     * @return
     */
    @Transactional
    public ProcedureTemplateDO editProcedureTemplate(String templateId, String procedureTemplateData) {

        // If template not exist, will create new template
        ProcedureTemplateDO existingProcedureTemplateDO = procedureTemplateRepository.findById(templateId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ProcedureTemplateDTO editProcedureTemplateDTO = objectMapper.readValue(procedureTemplateData, ProcedureTemplateDTO.class);

            existingProcedureTemplateDO = procedureTemplateConverter.convertExistingDOFromDTO(existingProcedureTemplateDO, editProcedureTemplateDTO);
            existingProcedureTemplateDO.setGmt_modified(new Date());

            if(editProcedureTemplateDTO.getCartaFungsi() != null) {
                FlowChartDO flowChartDO = null;

                if(existingProcedureTemplateDO.getCartaFungsi() != null && !existingProcedureTemplateDO.getCartaFungsi().getFlowChartId().isBlank()) {
                    flowChartDO = flowChartService.editFlowChart(existingProcedureTemplateDO.getCartaFungsi().getFlowChartId(), editProcedureTemplateDTO.getCartaFungsi());
                } else {
                    flowChartDO = flowChartService.addFlowChart(editProcedureTemplateDTO.getCartaFungsi());
                }
                existingProcedureTemplateDO.setCartaFungsi(flowChartDO);
            }

            return procedureTemplateRepository.save(existingProcedureTemplateDO);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);

        }

    }

    @Transactional
    public boolean deleteProcedureTemplate(String procedureTemplateId) {
        ProcedureTemplateDO procedureTemplateDO = procedureTemplateRepository.findById(procedureTemplateId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

        boolean success = flowChartService.deleteFlowChart(procedureTemplateDO.getCartaFungsi().getFlowChartId());

        if(!success) {
            return false;
        }

//        procedureTemplateRepository.delete(procedureTemplateDO);
//
//        return procedureTemplateRepository.findById(procedureTemplateId).isEmpty();

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(procedureTemplateId)), ProcedureTemplateDO.class);

        return result.getDeletedCount() > 0;
    }

}
