package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowChartData {
    private String id;

    private String title;

    private List<Node> nodes;

    private List<Edge> edges;
}
