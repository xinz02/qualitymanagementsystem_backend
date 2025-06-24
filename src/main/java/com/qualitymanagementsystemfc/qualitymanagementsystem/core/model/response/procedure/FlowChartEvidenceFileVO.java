package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import lombok.Data;

import java.util.List;

@Data
public class FlowChartEvidenceFileVO {
    private String flowChart;

    private List<NodeEvidenceFileInfoVO> nodeFiles;
}
