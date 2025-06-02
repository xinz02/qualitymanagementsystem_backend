package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {

    private String id;

    private String type;

    private Position position;

    private Measured measured;

    private Map<String, String> data;



}
