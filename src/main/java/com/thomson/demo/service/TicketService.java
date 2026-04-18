package com.thomson.demo.service;

import com.thomson.demo.entity.*;
import com.thomson.demo.enums.TicketStatus;
import com.thomson.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final AuditLogRepository auditLogRepository;

    public Ticket createTicket(Ticket ticket) {
        SlaPolicy policy = slaPolicyRepository
                .findByPriority(ticket.getPriority())
                .orElseThrow(() -> new RuntimeException("SLA policy not found for priority: " + ticket.getPriority()));

        ticket.setResponseDueAt(ticket.getCreatedAt().plusHours(policy.getResponseTimeHours()));
        ticket.setResolutionDueAt(ticket.getCreatedAt().plusHours(policy.getResolutionTimeHours()));

        Ticket saved = ticketRepository.save(ticket);

        saveAuditLog(saved, saved.getCustomer(), "Ticket created by " + saved.getCustomer().getName());

        return saved;
    }

    public Ticket assignTicket(UUID ticketId, UUID agentId, User admin) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        ticket.setAssignedAgent(agent);
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket saved = ticketRepository.save(ticket);

        saveAuditLog(saved, admin, "Ticket assigned to " + agent.getName() + " by " + admin.getName());

        return saved;
    }

    public Ticket updateStatus(UUID ticketId, TicketStatus newStatus, User performedBy) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus(newStatus);

        Ticket saved = ticketRepository.save(ticket);

        saveAuditLog(saved, performedBy, "Status changed to " + newStatus + " by " + performedBy.getName());

        return saved;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByCustomer(UUID customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }

    public List<Ticket> getTicketsByAgent(UUID agentId) {
        return ticketRepository.findByAssignedAgentId(agentId);
    }

    public List<Ticket> getBreachedTickets() {
        return ticketRepository.findBySlaBreachedTrue();
    }

    private void saveAuditLog(Ticket ticket, User performedBy, String action) {
        AuditLog log = AuditLog.builder()
                .ticket(ticket)
                .performedBy(performedBy)
                .action(action)
                .build();
        auditLogRepository.save(log);
    }
}
