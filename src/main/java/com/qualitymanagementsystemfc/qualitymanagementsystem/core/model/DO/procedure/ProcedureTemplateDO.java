package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.flowchart.FlowChartDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure.PindaanDokumen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "proceduretemplatedata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureTemplateDO {

    @Id
    private String templateId;

    private String namaDokumen;

    private String nomborDokumen;

//    @DocumentReference(lazy = true)
//    private List<PindaanDokumen> pindaanDokumenList;

    @DocumentReference(lazy = true)
    private FlowChartDO cartaFungsi;

    private String tujuan;

    private String objektif;

    private String skop;

    private String terminologi;

    private String singkatan;

    private String rujukan;

    private String prosedur;

    private String rekodDanSimpanan;

    private String lampiran;

    private Date gmt_create;

    private Date gmt_modified;

}
