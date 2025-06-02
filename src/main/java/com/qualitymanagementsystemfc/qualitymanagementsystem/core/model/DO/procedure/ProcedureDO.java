package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.CategoryDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.ModuleDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "procedure")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureDO {

    @Id
    private String procedureId;

    private String procedureNumber;

    private String procedureName;

    @DocumentReference(lazy = false)
    private ModuleDO module;

    @DocumentReference(lazy = false)
    private CategoryDO category;

    private List<String> viewPrivilege;

    @DocumentReference(lazy = false)
    private List<UserDO> assignTo;

    @DocumentReference(lazy = false)
    private ProcedureTemplateDO procedureTemplateData;

//    private byte procedureFile;
    private String fileId;

    @DocumentReference(lazy = false)
    private UserDO approver;

    private String approveStatus;

    private Date gmt_create;

    private Date gmt_modified;


}
