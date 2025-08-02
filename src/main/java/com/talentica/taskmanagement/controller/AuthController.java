package com.talentica.taskmanagement.controller;



import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Service;
import com.talentica.taskmanagement.dto.request.LoginRequest;
import com.talentica.taskmanagement.dto.request.UserRegistrationRequest;
import com.talentica.taskmanagement.dto.response.LoginResponse;
import com.talentica.taskmanagement.dto.response.UserResponse;
import com.talentica.taskmanagement.security.JwtTokenProvider;
import com.talentica.taskmanagement.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "Authentication", description = "Authentication management endpoints")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username/email and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserResponse userResponse = userService.getUserByUsername(authentication.getName());

        return ResponseEntity.ok(new LoginResponse(jwt, userResponse));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register new user", description = "Register a new user (Admin only)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse userResponse = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userResponse);
    }
}
