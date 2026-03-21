package com.psycology.backend.repository;

import com.psycology.backend.entity.CoursePurchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursePurchaseRepository extends JpaRepository<CoursePurchase, Long> {
}
