package com.psycology.backend.repository;

import com.psycology.backend.entity.RazorpayOrderMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RazorpayOrderMappingRepository extends JpaRepository<RazorpayOrderMapping, Long> {
    Optional<RazorpayOrderMapping> findByRazorpayOrderId(String razorpayOrderId);
}
