package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.FlowChartData;
import lombok.Data;

import java.util.List;

@Data
public class FlowChartDTO {

    private String flowChartId;

    private FlowChartData mainFlowChart;

    private List<FlowChartData> subFlowCharts;
}
