package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDO>> allUsers() {
        return new ResponseEntity<List<UserDO>>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/userID/{name}")
    public ResponseEntity<UserDO> searchByUserName(@PathVariable String name) {
        return new ResponseEntity<UserDO>(userService.getUserByName(name), HttpStatus.OK);
    }
}
