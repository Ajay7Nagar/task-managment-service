package com.talentica.taskmanagement.controller;

import com.talentica.taskmanagement.dto.request.LoginRequest;
import com.talentica.taskmanagement.dto.request.UserRegistrationRequest;
import com.talentica.taskmanagement.dto.response.LoginResponse;
import com.talentica.taskmanagement.dto.response.UserResponse;
import com.talentica.taskmanagement.security.JwtTokenProvider;
import com.talentica.taskmanagement.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @ApiOperation(value = "User login", notes = "Authenticate user with username/email and password")
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
//    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation(value = "Register new user", notes = "Register a new user (Admin only)")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.registerUser(registrationRequest);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @ApiOperation(value = "Get current user", notes = "Get current authenticated user information")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserResponse userResponse = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userResponse);
    }
}
