package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "form")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Form {

    @Id
    private String formId;

    private String formName;

    private String viewPrivilege;

    private byte form;

    private String moduleId;

    private Date gmt_create;

    private Date gmt_modified;
}
