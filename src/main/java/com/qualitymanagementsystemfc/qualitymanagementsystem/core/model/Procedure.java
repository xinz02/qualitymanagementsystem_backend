package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "procedure")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Procedure {

    @Id
    private String procedureID;

    private String procedureName;

    private String viewPrivilege;

//    private String author;

    private Date gmt_create;

    private Date gmt_modified;

    private String moduleID;

    private String categoryID;

    private String version;

    private List<String> assignTo;

    private Map<String, Object> templateData;

    private byte pdf;

    private String approverID;

    private String approveStatus;

}
