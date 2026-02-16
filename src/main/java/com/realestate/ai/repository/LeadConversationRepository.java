package com.realestate.ai.repository;

import com.realestate.ai.model.LeadConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadConversationRepository
        extends JpaRepository<LeadConversation, Long> {

    List<LeadConversation> findByLead_Id(Long leadId);
}
