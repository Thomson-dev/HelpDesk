package com.thomson.demo.entity;

import com.thomson.demo.enums.IssueCategory;
import com.thomson.demo.enums.Priority;
import com.thomson.demo.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime responseDueAt;

    private LocalDateTime resolutionDueAt;

    @Column(nullable = false)
    private boolean slaBreached = false;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User assignedAgent;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = TicketStatus.OPEN;
    }
}
