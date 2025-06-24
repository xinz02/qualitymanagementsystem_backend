package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import lombok.Data;

import java.util.List;

@Data
public class ProcedureTemplateDTO {

    private String namaDokumen;

    private String nomborDokumen;

//    private List<PindaanDokumen> pindaanDokumen;

    private FlowChartDTO cartaFungsi;

    private String tujuan;

    private String objektif;

    private String skop;

    private String terminologi;

    private String singkatan;

    private String rujukan;

    private String prosedur;

    private String rekodDanSimpanan;

    private String lampiran;

}
