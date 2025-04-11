package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String username;
    private String role;
}
