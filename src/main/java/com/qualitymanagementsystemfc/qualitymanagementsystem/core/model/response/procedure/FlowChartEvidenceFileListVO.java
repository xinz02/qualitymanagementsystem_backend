package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import lombok.Data;

import java.util.List;

@Data
public class FlowChartEvidenceFileListVO {

    private String flowChartId;

    private FlowChartEvidenceFileVO mainFlowChartEvidenceFile;

    private List<FlowChartEvidenceFileVO> subFlowChartsEvidenceFile;
}
