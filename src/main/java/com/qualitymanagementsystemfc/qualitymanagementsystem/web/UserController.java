package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.converter.UserConverter;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.PasswordResetToken;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.models.user.User;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.ForgotPasswordRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.LoginRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.ResetPasswordRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.AddUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.DeleteUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.user.EditUserRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.auth.JwtResponse;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.UserRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.EmailService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.PasswordResetTokenService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.impl.UserDetailsImpl;
import com.qualitymanagementsystemfc.qualitymanagementsystem.utils.CommonApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

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

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param request containing username and password
     * @return a ResponseEntity with JWT token and user info if successful,
     * or an error message with the appropriate HTTP status if authentication fails
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                    .toList();

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authorities: " + auth.getAuthorities());
            System.out.println("Is Authenticated: " + auth.isAuthenticated());
            System.out.println("Roles: " + roles);


            JwtResponse res = new JwtResponse();
            res.setToken(jwt);
            res.setId(userDetails.getId());
            res.setUsername(userDetails.getUsername());
            res.setRole(userDetails.getRole());

            return ResponseEntity.ok(res);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password. Please try again later."));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred. Please try again later."));
        }
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        UserDO user = userService.getUserByEmail(request.getEmail());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "This email is not registered. Please retype."));
        }

        String token = passwordResetTokenService.createToken(user);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "A password reset link has been sent before, please check your mailbox."));
        }

        emailService.sendPasswordResetEmail(user.getEmail(), token, false);


        return ResponseEntity.ok().body(Map.of("message", "A password reset link has been sent, please check your mailbox."));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (!passwordResetTokenService.isTokenValid(request.getToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid or expired token. Please try again."));
        }

        PasswordResetToken resetToken = passwordResetTokenService.getPasswordTokenByToken(request.getToken());

        UserDO user = resetToken.getUser();

        if (user != null) {
            userService.resetPassword(user, passwordEncoder.encode(request.getPassword()));
            passwordResetTokenService.deleteToken(resetToken.getId());
            return ResponseEntity.ok().body(Map.of("success", "Password Reset Successfully!"));

        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Reset Password Fail. Please try again later."));
    }

    @PostMapping("/editUser")
    public ResponseEntity<CommonApiResult<User>> editUser(@RequestBody EditUserRequest request) {
        CommonApiResult<User> res = new CommonApiResult<>();

        boolean existedEmail = userService.existByEmail(request.getUser().getEmail());
        boolean existedUsername = userService.existByUsername(request.getUser().getUsername());

        if (existedEmail) {
            res.setMessage("This email is registered. Unable to create again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        } else if (existedUsername) {
            res.setMessage("This username is registered. Unable to create again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
        }

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
            boolean existedEmail = userService.existByEmail(request.getUser().getEmail());
            boolean existedUsername = userService.existByUsername(request.getUser().getUsername());

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
