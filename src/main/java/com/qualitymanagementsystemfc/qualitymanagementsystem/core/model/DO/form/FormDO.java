package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.form;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "form")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDO {

    @Id
    private String formId;

    private String formNumber;

    private String formName;

    @DocumentReference(lazy = false)
    private ModuleDO module;

    @DocumentReference(lazy = false)
    private CategoryDO category;

    private List<String> viewPrivilege;

    @DocumentReference(lazy = false)
    private UserDO personInCharge;

    @DocumentReference(lazy = false)
    private ProcedureDO relatedProcedure;

    private String fileId;

    private Date gmt_create;

    private Date gmt_modified;

}
