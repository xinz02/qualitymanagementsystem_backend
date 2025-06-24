package com.qualitymanagementsystemfc.qualitymanagementsystem.service;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.UserConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserConverter userConverter;

    public List<User> getAllUsers() {
        List<UserDO> userDOList = userRepository.findAll();

        return userDOList.stream().map(user -> {
            return userConverter.convertDOToModel(user);
        }).toList();
    }

    public Optional<UserDO> getUserByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    public Optional<UserDO> getUserByName(String name) {
        return userRepository.findByUsername(name);
    }

    public UserDO getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void resetPassword(UserDO user, String newPassword) {
        user.setPassword(newPassword);
        user.setGmt_modified(new Date());
        userRepository.save(user);
    }

    public User editUser(User user) {
        Optional<UserDO> existingUserDO = userRepository.findById(user.getUserId());

        UserDO userDO = existingUserDO.get();

        userDO.setName(user.getName());
        userDO.setUsername(user.getUsername());
        userDO.setEmail(user.getEmail());
        userDO.setRole(user.getRole());
        userDO.setGmt_modified(new Date());

        return userConverter.convertDOToModel(userRepository.save(userDO));
    }

    public UserDO addUser(User user) {
        UserDO userDO = userConverter.convertModelToDO(user);
        userDO.setPassword(null);  // will send email to user to set themselves
        userDO.setGmt_create(new Date());
        userDO.setGmt_modified(new Date());

        return userRepository.save(userDO);
    }

    public boolean deleteUser(String userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public UserDO findByUserId(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User does not exists."));
    }

    public List<UserDO> findAllByUserId(List<String> userId) {
        return userRepository.findAllById(userId);
    }

}
