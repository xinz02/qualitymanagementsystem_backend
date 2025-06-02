package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge {

    private String id;

    private String source;

    private String sourceHandle;

    private String target;

    private String targetHandle;

    private String type;

    private MarkerEnd markerEnd;

    private Style style;
}
