package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import lombok.Data;

import java.util.List;

@Data
public class AddVersionRequest {
    private String versi;

    private List<String> assignedTo;

//    private String butiran;
}
