package com.realestate.ai.repository;

import com.realestate.ai.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByProjectIdAndActiveTrue(Long projectId);
}
