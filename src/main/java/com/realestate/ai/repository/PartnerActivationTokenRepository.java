package com.realestate.ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.ai.model.PartnerActivationToken;

public interface
PartnerActivationTokenRepository
extends JpaRepository
<PartnerActivationToken,Long>{

Optional<PartnerActivationToken>
findByToken(String token);
}

