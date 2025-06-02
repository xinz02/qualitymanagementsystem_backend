package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "module")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDO {

    @Id
    private String moduleId;

    private String moduleName;

    private List<String> viewPrivilege;

    @DocumentReference(lazy = true)
    private List<CategoryDO> categories;

    private Date gmt_create;

    private Date gmt_modified;
}
