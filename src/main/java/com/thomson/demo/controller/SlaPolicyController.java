package com.thomson.demo.controller;

import com.thomson.demo.dto.SlaPolicyRequest;
import com.thomson.demo.dto.SlaPolicyResponse;
import com.thomson.demo.service.SlaPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sla-policies")
@RequiredArgsConstructor
public class SlaPolicyController {

    private final SlaPolicyService slaPolicyService;

    @PostMapping
    public ResponseEntity<SlaPolicyResponse> createPolicy(
            @RequestBody SlaPolicyRequest request) {
        return ResponseEntity.ok(slaPolicyService.createPolicy(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SlaPolicyResponse> updatePolicy(
            @PathVariable Long id,
            @RequestBody SlaPolicyRequest request) {
        return ResponseEntity.ok(slaPolicyService.updatePolicy(id, request));
    }

    @GetMapping
    public ResponseEntity<List<SlaPolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(slaPolicyService.getAllPolicies());
    }
}
