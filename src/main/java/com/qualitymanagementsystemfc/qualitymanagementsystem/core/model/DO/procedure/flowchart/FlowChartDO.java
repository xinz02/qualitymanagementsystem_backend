package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart.FlowChartData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "flowchart")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowChartDO {

    @Id
    private String flowChartId;

    private FlowChartData mainFlowChart;

    private List<FlowChartData> subFlowCharts;
}
