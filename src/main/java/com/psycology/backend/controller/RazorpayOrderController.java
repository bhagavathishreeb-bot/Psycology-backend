package com.psycology.backend.controller;

import com.psycology.backend.dto.CreateOrderRequestDto;
import com.psycology.backend.dto.VerifyPaymentRequestDto;
import com.psycology.backend.exception.RazorpayException;
import com.psycology.backend.service.RazorpayOrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Razorpay Orders API integration.
 * - POST /api/razorpay/create-order : Create order, returns order_id, amount (paise), key_id
 * - POST /api/razorpay/verify-payment : Verify payment signature
 */
@RestController
@RequestMapping("/api/razorpay")
public class RazorpayOrderController {

    private static final Logger log = LoggerFactory.getLogger(RazorpayOrderController.class);

    private final RazorpayOrderService razorpayOrderService;

    public RazorpayOrderController(RazorpayOrderService razorpayOrderService) {
        this.razorpayOrderService = razorpayOrderService;
    }

    /**
     * Create a Razorpay order.
     * Accepts amount in INR, converts to paise, creates order via Razorpay API.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequestDto request) {
        try {
            Map<String, Object> result = razorpayOrderService.createOrder(
                    request.getAmount(),
                    request.getReceipt()
            );
            return ResponseEntity.ok(result);
        } catch (RazorpayException e) {
            log.warn("Razorpay create order failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        }
    }

    /**
     * Verify payment signature after successful payment.
     * Call this from your frontend after Razorpay handler returns.
     */
    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@Valid @RequestBody VerifyPaymentRequestDto request) {
        try {
            boolean isValid = razorpayOrderService.verifyPayment(
                    request.getRazorpayOrderId(),
                    request.getRazorpayPaymentId(),
                    request.getRazorpaySignature()
            );

            if (isValid) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment verified successfully"
                ));
            } else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "success", false,
                                "message", "Payment verification failed"
                        ));
            }
        } catch (RazorpayException e) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        }
    }

    @ExceptionHandler(RazorpayException.class)
    public ResponseEntity<?> handleRazorpayException(RazorpayException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("success", false, "error", e.getMessage()));
    }
}
