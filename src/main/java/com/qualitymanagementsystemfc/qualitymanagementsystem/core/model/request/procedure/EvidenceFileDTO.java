package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import lombok.Data;

@Data
public class EvidenceFileDTO {

    private String flowChartId;

    private String nodeId;

    private String flowChart;

    private String personInCharge;

    private String semester;
}
