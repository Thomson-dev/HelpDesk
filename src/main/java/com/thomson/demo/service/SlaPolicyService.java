package com.thomson.demo.service;

import com.thomson.demo.dto.SlaPolicyRequest;
import com.thomson.demo.dto.SlaPolicyResponse;
import com.thomson.demo.entity.SlaPolicy;
import com.thomson.demo.repository.SlaPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlaPolicyService {

    private final SlaPolicyRepository slaPolicyRepository;

    public SlaPolicyResponse createPolicy(SlaPolicyRequest request) {
        if (slaPolicyRepository.findByPriority(request.getPriority()).isPresent()) {
            throw new RuntimeException("SLA policy already exists for priority: "
                    + request.getPriority());
        }

        SlaPolicy policy = SlaPolicy.builder()
                .priority(request.getPriority())
                .responseTimeHours(request.getResponseTimeHours())
                .resolutionTimeHours(request.getResolutionTimeHours())
                .build();

        return mapToResponse(slaPolicyRepository.save(policy));
    }

    public SlaPolicyResponse updatePolicy(Long id, SlaPolicyRequest request) {
        SlaPolicy policy = slaPolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SLA policy not found"));

        policy.setResponseTimeHours(request.getResponseTimeHours());
        policy.setResolutionTimeHours(request.getResolutionTimeHours());

        return mapToResponse(slaPolicyRepository.save(policy));
    }

    public List<SlaPolicyResponse> getAllPolicies() {
        return slaPolicyRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SlaPolicyResponse mapToResponse(SlaPolicy policy) {
        return SlaPolicyResponse.builder()
                .id(policy.getId())
                .priority(policy.getPriority())
                .responseTimeHours(policy.getResponseTimeHours())
                .resolutionTimeHours(policy.getResolutionTimeHours())
                .build();
    }
}
