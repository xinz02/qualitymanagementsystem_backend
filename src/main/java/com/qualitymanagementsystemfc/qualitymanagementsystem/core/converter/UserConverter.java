package com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User convertDOToModel(UserDO userDO) {
        User user = new User();
        user.setUserId(userDO.getUserId());
        user.setName(userDO.getName());
        user.setUsername(userDO.getUsername());
        user.setEmail(userDO.getEmail());
        user.setRole(userDO.getRole());
        return user;
    }

    public UserDO convertModelToDO(User user) {
        UserDO userDO = new UserDO();
        userDO.setName(user.getName());
        userDO.setUsername(user.getUsername());
        userDO.setEmail(user.getEmail());
        userDO.setRole(user.getRole());
        return userDO;
    }
}
