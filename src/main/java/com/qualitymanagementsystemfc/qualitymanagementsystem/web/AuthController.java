package com.qualitymanagementsystemfc.qualitymanagementsystem.web;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.PasswordResetToken;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.DO.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.ForgotPasswordRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.LoginRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.auth.ResetPasswordRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.auth.JwtResponse;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.EmailService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.PasswordResetTokenService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.impl.UserDetailsImpl;
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
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EmailService emailService;

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

        if (user != null) {
            String token = passwordResetTokenService.createToken(user);

            if (token == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "A password reset link has been sent before, please check your mailbox."));
            }

            emailService.sendPasswordResetEmail(user.getEmail(), token, false);
        }

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

}
