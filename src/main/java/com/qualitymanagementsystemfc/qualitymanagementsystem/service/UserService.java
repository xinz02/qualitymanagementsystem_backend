package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDO> getAllUsers() {
        return userRepository.findAll();
    }

    public UserDO getUserByName(String name) {
        return userRepository.findByName(name);
    }
}
