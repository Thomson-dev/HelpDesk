package com.thomson.demo.controller;

import com.thomson.demo.dto.CreateTicketRequest;
import com.thomson.demo.dto.TicketResponse;
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
    public ResponseEntity<TicketResponse> createTicket(@RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(ticketService.createTicket(request));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<TicketResponse>> getTicketsByAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/breached")
    public ResponseEntity<List<TicketResponse>> getBreachedTickets() {
        return ResponseEntity.ok(ticketService.getBreachedTickets());
    }

    @PutMapping("/{ticketId}/assign/{agentId}")
    public ResponseEntity<TicketResponse> assignTicket(
            @PathVariable UUID ticketId,
            @PathVariable UUID agentId) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketId, agentId));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable UUID ticketId,
            @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateStatus(ticketId, status));
    }
}
