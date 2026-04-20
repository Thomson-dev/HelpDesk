package com.thomson.demo.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    // Role is NOT accepted - always defaults to CUSTOMER
    // Use /api/auth/assign-role endpoint (ADMIN only) to change roles
}

