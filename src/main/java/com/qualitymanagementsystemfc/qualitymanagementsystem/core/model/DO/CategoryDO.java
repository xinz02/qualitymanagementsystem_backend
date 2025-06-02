package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.procedure.ProcedureDO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDO {

    @Id
    private String categoryId;

    private String categoryName;

    private String moduleId;

    @DocumentReference
    private List<ProcedureDO> procedureIdList;

    private Date gmt_create;

    private Date gmt_modified;
}
