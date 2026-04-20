package com.thomson.demo.dto;

import com.thomson.demo.enums.Role;
import lombok.Data;

@Data
public class AssignRoleRequest {
    private String email;
    private Role role;
}
