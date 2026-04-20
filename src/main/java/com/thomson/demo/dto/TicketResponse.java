package com.thomson.demo.dto;

import com.thomson.demo.enums.IssueCategory;
import com.thomson.demo.enums.Priority;
import com.thomson.demo.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TicketResponse {
    private UUID id;
    private String title;
    private String description;
    private IssueCategory category;
    private TicketStatus status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime responseDueAt;
    private LocalDateTime resolutionDueAt;
    private boolean slaBreached;
    private String customerName;
    private String assignedAgentName;
}
