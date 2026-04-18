package com.thomson.demo.repository;

import com.thomson.demo.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByTicketId(UUID ticketId);
    List<AuditLog> findByPerformedById(UUID userId);
}
