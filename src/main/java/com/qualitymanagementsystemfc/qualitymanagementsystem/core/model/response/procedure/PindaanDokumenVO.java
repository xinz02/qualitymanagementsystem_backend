package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureTemplateDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PindaanDokumenVO {

    private String versi;

    private String tarikh;

    private String butiran;

    private String deskripsiPerubahan;

    private List<User> disediakan;

    private User diluluskan;

    private List<User> assignTo;

    private User approver;

    private String approveStatus;

    private String description;

    private ProcedureTemplateVO procedureTemplateData;
}
