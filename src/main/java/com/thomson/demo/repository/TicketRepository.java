package com.thomson.demo.repository;

import com.thomson.demo.entity.Ticket;
import com.thomson.demo.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByCustomerId(UUID customerId);
    List<Ticket> findByAssignedAgentId(UUID agentId);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findBySlaBreachedTrue();
}
