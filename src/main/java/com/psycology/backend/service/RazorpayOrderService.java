package com.psycology.backend.service;

import com.psycology.backend.config.RazorpayProperties;
import com.psycology.backend.exception.RazorpayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Razorpay integration service.
 * Uses Razorpay REST API directly (no Java SDK).
 */
@Service
public class RazorpayOrderService {

    private static final String RAZORPAY_ORDERS_URL = "https://api.razorpay.com/v1/orders";
    private static final Logger log = LoggerFactory.getLogger(RazorpayOrderService.class);

    private final RazorpayProperties razorpayProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public RazorpayOrderService(RazorpayProperties razorpayProperties) {
        this.razorpayProperties = razorpayProperties;
    }

    /**
     * Create Razorpay order via REST API. Amount is converted from INR to paise.
     *
     * @param amountInr Amount in Indian Rupees (e.g. 599.00)
     * @param receipt   Optional receipt id (max 40 chars)
     * @return Map with order_id, amount (paise), key_id
     */
    public Map<String, Object> createOrder(BigDecimal amountInr, String receipt) {
        validateCredentials();

        // Convert INR to paise (1 INR = 100 paise)
        long amountPaise = amountInr.multiply(BigDecimal.valueOf(100)).longValue();
        if (amountPaise < 100) {
            throw new RazorpayException("Amount must be at least ₹1");
        }

        String receiptId = receipt != null ? receipt : "rcpt_" + System.currentTimeMillis();
        return createOrderViaRest(amountPaise, receiptId);
    }

    private Map<String, Object> createOrderViaRest(long amountPaise, String receipt) {
        String credentials = razorpayProperties.getKeyId() + ":" + razorpayProperties.getKeySecret();
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);
        Map<String, Object> body = Map.of("amount", amountPaise, "currency", "INR", "receipt", receipt);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(RAZORPAY_ORDERS_URL, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
            Map<String, Object> result = response.getBody();
            if (result == null || !result.containsKey("id")) throw new RazorpayException("Invalid Razorpay response");
            return Map.of("order_id", String.valueOf(result.get("id")), "amount", amountPaise, "key_id", razorpayProperties.getKeyId());
        } catch (Exception e) {
            log.error("Razorpay create order failed: {}", e.getMessage());
            throw new RazorpayException("Failed to create order: " + e.getMessage());
        }
    }

    /**
     * Verify payment signature using Razorpay's HMAC-SHA256 algorithm.
     * Formula: HMAC_SHA256(order_id + "|" + payment_id, secret)
     */
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        validateCredentials();

        try {
            String payload = orderId + "|" + paymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayProperties.getKeySecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = bytesToHex(hash);

            return expectedSignature.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }

    private void validateCredentials() {
        if (razorpayProperties.getKeyId() == null || razorpayProperties.getKeyId().isBlank() ||
                razorpayProperties.getKeySecret() == null || razorpayProperties.getKeySecret().isBlank()) {
            throw new RazorpayException("Razorpay credentials not configured. Set RAZORPAY_KEY_ID and RAZORPAY_KEY_SECRET.");
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
