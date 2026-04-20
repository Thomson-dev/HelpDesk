package com.thomson.demo.service;

import com.thomson.demo.dto.CreateTicketRequest;
import com.thomson.demo.dto.TicketResponse;
import com.thomson.demo.entity.*;
import com.thomson.demo.enums.IssueCategory;
import com.thomson.demo.enums.Priority;
import com.thomson.demo.enums.TicketStatus;
import com.thomson.demo.repository.*;
import com.thomson.demo.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final AuditLogRepository auditLogRepository;
    private final SecurityUtils securityUtils;

    public TicketResponse createTicket(CreateTicketRequest request) {
        User customer = securityUtils.getLoggedInUser();

        Priority priority = resolvePriority(request.getCategory());

        SlaPolicy policy = slaPolicyRepository
                .findByPriority(priority)
                .orElseThrow(() -> new RuntimeException("SLA policy not found"));

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(priority)
                .customer(customer)
                .build();

        // Save first to trigger @PrePersist and populate createdAt
        Ticket saved = ticketRepository.save(ticket);

        // Now calculate SLA dates using the populated createdAt
        saved.setResponseDueAt(saved.getCreatedAt()
                .plusHours(policy.getResponseTimeHours()));
        saved.setResolutionDueAt(saved.getCreatedAt()
                .plusHours(policy.getResolutionTimeHours()));

        // Save again with updated SLA dates
        saved = ticketRepository.save(saved);

        saveAuditLog(saved, customer, "Ticket created by "
                + customer.getName() + " — Category: " + request.getCategory());

        return mapToResponse(saved);
    }

    public TicketResponse assignTicket(UUID ticketId, UUID agentId) {
        User admin = securityUtils.getLoggedInUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        ticket.setAssignedAgent(agent);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket saved = ticketRepository.save(ticket);

        saveAuditLog(saved, admin, "Ticket assigned to "
                + agent.getName() + " by " + admin.getName());

        return mapToResponse(saved);
    }

    public TicketResponse updateStatus(UUID ticketId, TicketStatus newStatus) {
        User performer = securityUtils.getLoggedInUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(newStatus);

        Ticket saved = ticketRepository.save(ticket);

        saveAuditLog(saved, performer, "Status changed to "
                + newStatus + " by " + performer.getName());

        return mapToResponse(saved);
    }

    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyTickets() {
        User customer = securityUtils.getLoggedInUser();
        return ticketRepository.findByCustomerId(customer.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getMyAgentTickets() {
        User agent = securityUtils.getLoggedInUser();
        return ticketRepository.findByAssignedAgentId(agent.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getBreachedTickets() {
        return ticketRepository.findBySlaBreachedTrue()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Priority resolvePriority(IssueCategory category) {
        return switch (category) {
            case ORDER_NOT_DELIVERED,
                 PAYMENT_NOT_CONFIRMED -> Priority.CRITICAL;
            case WRONG_ITEM_RECEIVED,
                 REFUND_REQUEST,
                 DAMAGED_ITEM -> Priority.HIGH;
            case ACCOUNT_ISSUE -> Priority.MEDIUM;
            case GENERAL_ENQUIRY -> Priority.LOW;
        };
    }

    private void saveAuditLog(Ticket ticket, User performedBy, String action) {
        AuditLog log = AuditLog.builder()
                .ticket(ticket)
                .performedBy(performedBy)
                .action(action)
                .build();
        auditLogRepository.save(log);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .category(ticket.getCategory())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .responseDueAt(ticket.getResponseDueAt())
                .resolutionDueAt(ticket.getResolutionDueAt())
                .slaBreached(ticket.isSlaBreached())
                .customerName(ticket.getCustomer().getName())
                .assignedAgentName(ticket.getAssignedAgent() != null
                        ? ticket.getAssignedAgent().getName() : null)
                .build();
    }
}
