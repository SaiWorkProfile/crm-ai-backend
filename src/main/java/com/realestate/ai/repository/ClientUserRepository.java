package com.realestate.ai.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.ai.model.ClientUser;

public interface ClientUserRepository
extends JpaRepository<ClientUser,Long>{

Optional<ClientUser>
findByEmail(String email);
}
