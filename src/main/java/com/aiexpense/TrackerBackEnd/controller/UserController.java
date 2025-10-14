package com.aiexpense.trackerbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;



import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.aiexpense.trackerbackend.entities.Users;
import com.aiexpense.trackerbackend.service.JwtService;
import com.aiexpense.trackerbackend.service.UserMapper;
import com.aiexpense.trackerbackend.service.UserService;
import com.aiexpense.trackerbackend.service.dto.AuthResponse;
import com.aiexpense.trackerbackend.service.dto.LoginRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "User Authentication", description = "APIs for User Signup, Login, and Role-Based Dashboards")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Register a new user", description = "Allows users to sign up with their details")
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody Users user) throws Exception {
        if (!user.isEnabled()) { // Assuming `enabled` is a boolean field in the Users class
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please agree to the terms and conditions");
        }
        userService.register(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Login user", description = "Authenticate user and return a JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(loginRequest.email());
            var user = userService.findByEmail(loginRequest.email());
            var dto  = UserMapper.toDTO(user);
            return ResponseEntity.ok(new AuthResponse(token,dto));
        } else {
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
