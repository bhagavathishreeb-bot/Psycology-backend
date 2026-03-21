package com.psycology.backend.service;

import com.psycology.backend.config.RazorpayProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class RazorpayService {

    private static final String RAZORPAY_ORDERS_URL = "https://api.razorpay.com/v1/orders";
    private static final Logger log = LoggerFactory.getLogger(RazorpayService.class);

    private final RazorpayProperties razorpayProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public RazorpayService(RazorpayProperties razorpayProperties) {
        this.razorpayProperties = razorpayProperties;
    }

    /**
     * Creates a Razorpay order via REST API. Amount should be in paise (e.g. ₹599 = 59900).
     */
    public JSONObject createOrder(long amountPaise, String receipt, JSONObject notes) {
        if (razorpayProperties.getKeyId() == null || razorpayProperties.getKeyId().isBlank() ||
                razorpayProperties.getKeySecret() == null || razorpayProperties.getKeySecret().isBlank()) {
            throw new IllegalStateException("Razorpay credentials not configured");
        }

        String credentials = razorpayProperties.getKeyId() + ":" + razorpayProperties.getKeySecret();
        String encodedAuth = java.util.Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encodedAuth);

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amountPaise);
        body.put("currency", "INR");
        body.put("receipt", receipt);
        if (notes != null && !notes.isEmpty()) {
            Map<String, Object> notesMap = new HashMap<>();
            notes.keySet().forEach(k -> notesMap.put(k, notes.get(k)));
            body.put("notes", notesMap);
        }

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(RAZORPAY_ORDERS_URL, HttpMethod.POST, request, Map.class);
            Map<String, Object> result = response.getBody();
            if (result == null || !result.containsKey("id")) {
                throw new RuntimeException("Invalid response from Razorpay");
            }
            return new JSONObject(result);
        } catch (Exception e) {
            log.error("Razorpay create order failed: {}", e.getMessage());
            throw new RuntimeException("Razorpay order failed: " + e.getMessage(), e);
        }
    }

    /**
     * Verifies webhook signature. Use the raw request body as string.
     */
    public boolean verifyWebhookSignature(String body, String signature) {
        if (razorpayProperties.getWebhookSecret() == null || razorpayProperties.getWebhookSecret().isBlank()) {
            log.warn("Razorpay webhook secret not configured");
            return false;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayProperties.getWebhookSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(hash);
            return expectedSignature.equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public String getKeyId() {
        return razorpayProperties.getKeyId();
    }
}
