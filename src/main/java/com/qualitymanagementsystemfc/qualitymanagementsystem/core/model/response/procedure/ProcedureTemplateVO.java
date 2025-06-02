package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure.flowchart.FlowChartVO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
public class ProcedureTemplateVO {

    private String namaDokumen;

    private String nomborDokumen;

    //    @DocumentReference(lazy = true)
    private List<PindaanDokumen> pindaanDokumen;

    @DocumentReference(lazy = true)
    private FlowChartVO cartaFungsi;

    private String tujuan;

    private String objektif;

    private String skop;

    private String terminologi;

    private String singkatan;

    private String rujukan;

    private String prosedur;

    private String rekodDanSimpanan;

    private String lampiran;

//    private Date gmt_create;
//
//    private Date gmt_modified;
}
