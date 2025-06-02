package com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import lombok.Data;

@Data
public class AddUserRequest {
    private User user;
}
