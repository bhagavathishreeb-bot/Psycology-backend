package com.psycology.backend.repository;

import com.psycology.backend.entity.CareerApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerApplicationRepository extends JpaRepository<CareerApplication, Long> {
}
