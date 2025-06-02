package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.mongodb.client.result.DeleteResult;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.FlowChartConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.FlowChartDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.FlowChartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlowChartService {

    @Autowired
    private FlowChartRepository flowChartRepository;

    @Autowired
    private FlowChartConverter flowChartConverter;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Add new flowchart
     * @param flowChartDTO
     * @return
     */
    @Transactional
    public FlowChartDO addFlowChart(FlowChartDTO flowChartDTO) {
        if(flowChartDTO == null) {
            return null;
        }

        FlowChartDO flowChartDO = flowChartConverter.convertDTOToDO(flowChartDTO);

        return flowChartRepository.save(flowChartDO);
    }

    /**
     * Edit existing flow chart
     * @param flowChartId
     * @param editFlowChartDTO
     * @return
     */
    @Transactional
    public FlowChartDO editFlowChart(String flowChartId, FlowChartDTO editFlowChartDTO) {

        FlowChartDO existingFlowChartDO = flowChartRepository.findById(flowChartId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

        existingFlowChartDO = flowChartConverter.convertExistingDOFromDTO(existingFlowChartDO, editFlowChartDTO);

        return flowChartRepository.save(existingFlowChartDO);
    }

    /**
     * Delete flow chart
     * @param flowChartId
     * @return
     */
    @Transactional
    public boolean deleteFlowChart(String flowChartId) {
        FlowChartDO existingFlowChartDO = flowChartRepository.findById(flowChartId).orElseThrow(() -> new IllegalArgumentException("Procedure Template not found."));

//        flowChartRepository.delete(existingFlowChartDO);
//
//        return flowChartRepository.findById(flowChartId).isEmpty();

        DeleteResult result = mongoTemplate.remove(Query.query(Criteria.where("_id").is(flowChartId)), FlowChartDO.class);

        return result.getDeletedCount() > 0;
    }
}
