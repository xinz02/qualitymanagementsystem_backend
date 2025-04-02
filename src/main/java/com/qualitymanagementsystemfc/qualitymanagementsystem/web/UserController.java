package com.qualitymanagementsystemfc.qualitymanagementsystem.web;

import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.UserDO;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.request.LoginRequest;
import com.qualitymanagementsystemfc.qualitymanagementsystem.core.model.response.JwtResponse;
import com.qualitymanagementsystemfc.qualitymanagementsystem.repository.UserRepository;
import com.qualitymanagementsystemfc.qualitymanagementsystem.security.JwtUtil;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.UserService;
import com.qualitymanagementsystemfc.qualitymanagementsystem.service.impl.UserDetailsImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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


//    @GetMapping
//    public ResponseEntity<List<UserDO>> allUsers() {
//        return new ResponseEntity<List<UserDO>>(userService.getAllUsers(), HttpStatus.OK);
//    }
//
//    @GetMapping("/userID/{name}")
//    public ResponseEntity<Optional<UserDO>> searchByUserName(@PathVariable String name) {
//        return new ResponseEntity<Optional<UserDO>>(userService.getUserByName(name), HttpStatus.OK);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            JwtResponse res = new JwtResponse();
            res.setToken(jwt);
            res.setId(userDetails.getId());
            res.setUsername(userDetails.getUsername());
            res.setRole(userDetails.getRole());

            return ResponseEntity.ok(res);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password. Please try again later."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred. Please try again later."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpRequest signUpRequest) {

        String hashedPassword = passwordEncoder.encode(signUpRequest.getPassword());
//        String roles = "ADMIN";

        UserDO user = new UserDO();
        user.setUsername(signUpRequest.getUsername());

        user.setPassword(hashedPassword);
        user.setRole(signUpRequest.getRole());
        user.setGmt_create(new Date());
        user.setGmt_modified(new Date());
        userRepository.save(user);
        return ResponseEntity.ok("User registered success");
    }

    @Data
    public static class SignUpRequest {
        private String name;
        private String username;
        private String password;
        private String role;
    }
}
