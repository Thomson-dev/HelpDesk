package com.thomson.demo.controller;

import com.thomson.demo.entity.*;
import com.thomson.demo.enums.TicketStatus;
import com.thomson.demo.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Ticket>> getTicketsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ticketService.getTicketsByCustomer(customerId));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<Ticket>> getTicketsByAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(ticketService.getTicketsByAgent(agentId));
    }

    @GetMapping("/breached")
    public ResponseEntity<List<Ticket>> getBreachedTickets() {
        return ResponseEntity.ok(ticketService.getBreachedTickets());
    }

    @PutMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<Ticket> assignTicket(
            @PathVariable UUID ticketId,
            @PathVariable UUID agentId,
            @RequestBody User admin) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, agentId, admin));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateStatus(
            @PathVariable UUID ticketId,
            @RequestParam TicketStatus status,
            @RequestBody User performedBy) {
        return ResponseEntity.ok(ticketService.updateStatus(ticketId, status, performedBy));
    }
}
