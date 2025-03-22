package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

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
public class Module {

    @Id
    private String moduleID;

    private String moduleName;

    private String viewPrivilege;

    @DocumentReference
    private List<Category> categoryIDList;

    private Date gmt_create;

    private Date gmt_modified;
}
