package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

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
public class Category {

    @Id
    private String categoryID;

    private String categoryName;

    private String moduleID;

    @DocumentReference
    private List<Procedure> procedureIDList;

    private Date gmt_create;

    private Date gmt_modified;
}
