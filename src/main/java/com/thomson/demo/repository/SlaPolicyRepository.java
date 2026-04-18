package com.thomson.demo.repository;

import com.thomson.demo.entity.SlaPolicy;
import com.thomson.demo.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SlaPolicyRepository extends JpaRepository<SlaPolicy, Long> {
    Optional<SlaPolicy> findByPriority(Priority priority);
}
