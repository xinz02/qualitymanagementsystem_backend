package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure.FlowChartDTO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.flowchart.FlowChartVO;
import org.springframework.stereotype.Component;

@Component
public class FlowChartConverter {

    public FlowChartDO convertDTOToDO(FlowChartDTO flowChartDTO) {
        FlowChartDO flowChartDO = new FlowChartDO();
        flowChartDO.setFlowChartId(flowChartDTO.getFlowChartId());
        flowChartDO.setMainFlowChart(flowChartDTO.getMainFlowChart());
        flowChartDO.setSubFlowCharts(flowChartDTO.getSubFlowCharts());

        return flowChartDO;
    }

    public FlowChartVO convertDOToVO(FlowChartDO flowChartDO) {
        FlowChartVO flowChartVO = new FlowChartVO();
        flowChartVO.setFlowChartId(flowChartDO.getFlowChartId());
        flowChartVO.setMainFlowChart(flowChartDO.getMainFlowChart());
        flowChartVO.setSubFlowCharts(flowChartDO.getSubFlowCharts());

        return flowChartVO;
    }

    public FlowChartDO convertExistingDOFromDTO(FlowChartDO existingFlowChartDO, FlowChartDTO editFlowChartDTO) {
        existingFlowChartDO.setFlowChartId(editFlowChartDTO.getFlowChartId());
        existingFlowChartDO.setMainFlowChart(editFlowChartDTO.getMainFlowChart());
        existingFlowChartDO.setSubFlowCharts(editFlowChartDTO.getSubFlowCharts());

        return existingFlowChartDO;
    }
}
