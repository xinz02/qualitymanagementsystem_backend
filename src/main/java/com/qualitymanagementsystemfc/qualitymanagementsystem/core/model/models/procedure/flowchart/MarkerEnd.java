package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.flowchart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkerEnd {

    private String color;

    /**
     * edge marker type
     */
    private String type;

    private double width;

    private double height;
}
