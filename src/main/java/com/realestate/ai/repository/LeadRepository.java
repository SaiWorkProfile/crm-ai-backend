package com.realestate.ai.repository;

import com.realestate.ai.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    long countByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<Lead> findByPhone(String phone);

    List<Lead> findByUpdatedAtBefore(LocalDateTime threshold);
}
