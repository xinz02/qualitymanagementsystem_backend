package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDO {

    @Id
    private String userID;

    private String name;

    private String username;

    private String password;

    private String role;

    private Date gmt_create;

    private Date gmt_modified;
}
