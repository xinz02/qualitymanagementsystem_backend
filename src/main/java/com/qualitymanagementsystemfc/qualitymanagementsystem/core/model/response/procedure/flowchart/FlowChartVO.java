package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.flowchart;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.FlowChartData;
import lombok.Data;

import java.util.List;

@Data
public class FlowChartVO {

    private FlowChartData mainFlowChart;

    private List<FlowChartData> subFlowCharts;
}
