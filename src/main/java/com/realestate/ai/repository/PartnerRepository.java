package com.realestate.ai.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.ai.model.Partner;

public interface PartnerRepository
extends JpaRepository<Partner,Long>{

Optional<Partner>
findByPincodeAndDeletedFalse(String pincode);

Optional<Partner>
findByEmail(String email);

List<Partner>
findByDeletedFalse();
boolean existsByEmailAndDeletedFalse(String email);
}
