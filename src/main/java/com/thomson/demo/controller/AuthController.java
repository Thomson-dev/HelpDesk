package com.thomson.demo.controller;

import com.thomson.demo.dto.AssignRoleRequest;
import com.thomson.demo.dto.AuthResponse;
import com.thomson.demo.dto.LoginRequest;
import com.thomson.demo.dto.RegisterRequest;
import com.thomson.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/assign-role")
    public ResponseEntity<AuthResponse> assignRole(@RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(authService.assignRole(request.getEmail(), request.getRole()));
    }
}
