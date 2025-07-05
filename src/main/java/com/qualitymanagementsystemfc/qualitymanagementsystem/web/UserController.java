package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.UserConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.AddUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.DeleteUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.EditUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.EmailService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.PasswordResetTokenService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserConverter userConverter;

    /**
     * Get List of Users
     *
     * @return allUsers
     */
    @GetMapping("/getAllUsers")
    public ResponseEntity<CommonApiResult<List<User>>> getAllUsers() {
        CommonApiResult<List<User>> res = new CommonApiResult<>();
        res.setData(userService.getAllUsers());

        return ResponseEntity.ok(res);
    }



    @PostMapping("/editUser")
    public ResponseEntity<CommonApiResult<User>> editUser(@RequestBody EditUserRequest request) {
        CommonApiResult<User> res = new CommonApiResult<>();

        User editedUser = userService.editUser(request.getUser());

        res.setData(editedUser);
        res.setMessage("Edited successfully!");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<CommonApiResult<Void>> deleteUser(@RequestBody DeleteUserRequest request) {
        CommonApiResult<Void> res = new CommonApiResult<>();

        boolean success = userService.deleteUser(request.getUserId());

        if (!success) {
            res.setMessage("Unable to delete user. User not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

        res.setMessage("User deleted successfully!");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/addUser")
    public ResponseEntity<CommonApiResult<User>> signup(@RequestBody AddUserRequest request) {

        CommonApiResult<User> res = new CommonApiResult<>();

        try {
            boolean existedEmail = userService.existByEmail(request.getUser().getEmail()) || userService.existByUsername(request.getUser().getUsername());
            boolean existedUsername = userService.existByEmail(request.getUser().getEmail()) || userService.existByUsername(request.getUser().getUsername());

            if (existedEmail) {
                res.setMessage("This email is registered. Unable to create again.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            } else if (existedUsername) {
                res.setMessage("This username is registered. Unable to create again.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
            }

            UserDO user = userService.addUser(request.getUser());

            if (user == null) {
                res.setMessage("Fail to add new user. Please try again later");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
            }

            res.setData(userConverter.convertDOToModel(user));

            String token = passwordResetTokenService.createToken(user);

            if (token == null) {
                res.setMessage("A password reset link has been sent before, please check your mailbox.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
            }

            emailService.sendPasswordResetEmail(user.getEmail(), token, true);

            res.setMessage("User added successfully!");
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setMessage("Unexpected error occured. Please try again later");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }


    }

}
