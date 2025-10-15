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
import com.aiexpense.trackerbackend.service.UserService;
import com.aiexpense.trackerbackend.service.dto.AuthResponse;
import com.aiexpense.trackerbackend.service.dto.LoginRequest;
import com.aiexpense.trackerbackend.service.dto.UserRegistrationDTO;

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
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDTO userDTO) throws Exception {
        if (!userDTO.enabled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Please agree to the terms and conditions");
        }

        // Convert DTO to entity
        Users user = Users.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .password(userDTO.password()) // Make sure to encode this in service
                .contact(userDTO.contact())
                .enabled(userDTO.enabled())
                .role("ROLE_CUSTOMER") // Set default role
                .build();

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
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
