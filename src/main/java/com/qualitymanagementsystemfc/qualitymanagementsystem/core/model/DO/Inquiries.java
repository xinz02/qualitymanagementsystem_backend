package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "inquiries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inquiries {

    @Id
    private String inquiriesId;

    private String userName;

    private String email;

    private String matricNumber;

    private String title;

    private String description;

    private String status;

    private Date gmt_create;

    private Date gmt_modified;
}
