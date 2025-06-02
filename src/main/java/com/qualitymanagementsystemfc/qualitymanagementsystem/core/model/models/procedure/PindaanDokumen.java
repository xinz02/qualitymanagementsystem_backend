package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Document(collection = "pindaandokumen")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PindaanDokumen {

//    @Id
//    private String pindaanId;

    private String pindaan;

    private String tarikh;

    private String butiran;

    private String disediakan;

    private String diluluskan;

}
