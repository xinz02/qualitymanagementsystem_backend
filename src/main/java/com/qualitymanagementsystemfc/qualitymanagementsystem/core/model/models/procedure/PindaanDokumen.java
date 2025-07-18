package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

//@Document(collection = "pindaandokumen")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PindaanDokumen {

//    @Id
//    private String pindaanId;

    private String versi;

    private String tarikh;

    private String butiran;

    private String deskripsiPerubahan;

    @DocumentReference(lazy = false)
    private List<UserDO> disediakan;

    @DocumentReference(lazy = false)
    private UserDO diluluskan;

    @DocumentReference(lazy = false)
    private List<UserDO> assignTo;

    @DocumentReference(lazy = false)
    private UserDO approver;

    private String approveStatus;

    private String description;

    @DocumentReference(lazy = true)
    private ProcedureTemplateDO procedureTemplateData;


}
