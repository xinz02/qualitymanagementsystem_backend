package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

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
    private String formID;

    private String formName;

    private String viewPrivilege;

    private byte form;

    private String moduleID;

    private Date gmt_create;

    private Date gmt_modified;
}
