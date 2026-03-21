package com.psycology.backend.controller;

import com.psycology.backend.dto.CoursePurchaseRequest;
import com.psycology.backend.entity.CoursePurchase;
import com.psycology.backend.repository.CoursePurchaseRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course-purchases")
public class CoursePurchaseController {

    private final CoursePurchaseRepository coursePurchaseRepository;

    public CoursePurchaseController(CoursePurchaseRepository coursePurchaseRepository) {
        this.coursePurchaseRepository = coursePurchaseRepository;
    }

    @PostMapping
    public ResponseEntity<?> createCoursePurchase(@Valid @RequestBody CoursePurchaseRequest request) {
        CoursePurchase purchase = new CoursePurchase();
        purchase.setCourseId(request.getCourseId());
        purchase.setCourseTitle(request.getCourseTitle());
        purchase.setPrice(request.getPrice());
        purchase.setOriginalPrice(request.getOriginalPrice());
        purchase.setCustomerName(request.getCustomerName());
        purchase.setCustomerEmail(request.getCustomerEmail());
        purchase.setCustomerPhone(request.getCustomerPhone());
        purchase.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "pending");
        purchase.setPaymentId(request.getPaymentId());

        purchase = coursePurchaseRepository.save(purchase);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", purchase.getId()));
    }
}
