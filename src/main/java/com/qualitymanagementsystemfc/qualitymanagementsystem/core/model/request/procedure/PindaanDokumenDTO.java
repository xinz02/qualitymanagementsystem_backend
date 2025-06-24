package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.procedure;

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
public class PindaanDokumenDTO {

    private String versi;

    private String tarikh;

    private String butiran;

    private String deskripsiPerubahan;

    private List<String> disediakan;

    private String diluluskan;

    private List<String> assignedTo;

}
