package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcedureTemplate {

    @Id
    private String templateID;

    private String templateVersion;

    private String authorID;

    private List<String> templateData;

    private Date gmt_create;

    private Date gmt_modified;
}
