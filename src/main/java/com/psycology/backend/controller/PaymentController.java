package com.psycology.backend.controller;

import com.psycology.backend.dto.CreateOrderRequest;
import com.psycology.backend.dto.VerifyPaymentRequestDto;
import com.psycology.backend.entity.RazorpayOrderMapping;
import com.psycology.backend.repository.BookingRepository;
import com.psycology.backend.repository.CoursePurchaseRepository;
import com.psycology.backend.repository.RazorpayOrderMappingRepository;
import com.psycology.backend.repository.ShopOrderRepository;
import com.psycology.backend.service.BrevoEmailService;
import com.psycology.backend.service.RazorpayOrderService;
import com.psycology.backend.service.RazorpayService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final RazorpayService razorpayService;
    private final RazorpayOrderService razorpayOrderService;
    private final CoursePurchaseRepository coursePurchaseRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final BookingRepository bookingRepository;
    private final RazorpayOrderMappingRepository mappingRepository;
    private final BrevoEmailService brevoEmailService;

    public PaymentController(RazorpayService razorpayService,
                             RazorpayOrderService razorpayOrderService,
                             CoursePurchaseRepository coursePurchaseRepository,
                             ShopOrderRepository shopOrderRepository,
                             BookingRepository bookingRepository,
                             RazorpayOrderMappingRepository mappingRepository,
                             BrevoEmailService brevoEmailService) {
        this.razorpayService = razorpayService;
        this.razorpayOrderService = razorpayOrderService;
        this.coursePurchaseRepository = coursePurchaseRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.bookingRepository = bookingRepository;
        this.mappingRepository = mappingRepository;
        this.brevoEmailService = brevoEmailService;
    }

    /**
     * Creates a Razorpay order. Call this after creating a course purchase or shop order.
     * Returns razorpayOrderId and key for frontend checkout.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            // Validate entity exists
            if ("course".equalsIgnoreCase(request.getOrderType())) {
                coursePurchaseRepository.findById(request.getEntityId())
                        .orElseThrow(() -> new IllegalArgumentException("Course purchase not found: " + request.getEntityId()));
            } else if ("shop".equalsIgnoreCase(request.getOrderType())) {
                shopOrderRepository.findById(request.getEntityId())
                        .orElseThrow(() -> new IllegalArgumentException("Shop order not found: " + request.getEntityId()));
            } else if ("booking".equalsIgnoreCase(request.getOrderType())) {
                bookingRepository.findById(request.getEntityId())
                        .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + request.getEntityId()));
            } else {
                throw new IllegalArgumentException("Invalid order type. Use 'course', 'shop' or 'booking'");
            }

            // Amount in paise (Razorpay uses smallest currency unit)
            long amountPaise = (long) (request.getAmount() * 100);

            JSONObject notes = new JSONObject();
            notes.put("order_type", request.getOrderType());
            notes.put("entity_id", request.getEntityId());

            var order = razorpayService.createOrder(amountPaise, request.getReceipt(), notes);
            String razorpayOrderId = String.valueOf(order.get("id"));

            // Store mapping for webhook lookup
            var mapping = new RazorpayOrderMapping();
            mapping.setRazorpayOrderId(razorpayOrderId);
            mapping.setOrderType(request.getOrderType().toLowerCase());
            mapping.setEntityId(request.getEntityId());
            mappingRepository.save(mapping);

            return ResponseEntity.ok(Map.of(
                    "razorpayOrderId", razorpayOrderId,
                    "razorpayKeyId", razorpayService.getKeyId(),
                    "amount", request.getAmount(),
                    "currency", "INR"
            ));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Payment gateway not configured"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to create Razorpay order: ", e);
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", msg != null ? msg : "Failed to create payment order"));
        }
    }

    /**
     * Verify payment signature after Razorpay checkout success.
     * Call from frontend with razorpay_order_id, razorpay_payment_id, razorpay_signature.
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
                mappingRepository.findByRazorpayOrderId(request.getRazorpayOrderId()).ifPresent(mapping ->
                        updatePaymentStatus(
                                mapping.getOrderType(),
                                mapping.getEntityId(),
                                "paid",
                                request.getRazorpayPaymentId()));
                return ResponseEntity.ok(Map.of("success", true, "message", "Payment verified successfully"));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Payment verification failed"));
        } catch (Exception e) {
            log.error("Verify payment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Razorpay webhook. Configure this URL in Razorpay Dashboard.
     * Events: payment.captured, payment.failed
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String rawBody,
                                        @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        if (signature == null || signature.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (!razorpayService.verifyWebhookSignature(rawBody, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            var json = new JSONObject(rawBody);
            String event = json.optString("event");
            var payload = json.optJSONObject("payload");

            if (payload == null) return ResponseEntity.ok().build();

            if ("payment.captured".equals(event) || "payment.failed".equals(event)) {
                var paymentObj = payload.optJSONObject("payment");
                if (paymentObj != null) {
                    // Razorpay payload: payload.payment.entity or payload.payment
                    var entity = paymentObj.optJSONObject("entity");
                    if (entity == null) entity = paymentObj;
                    String orderId = entity.optString("order_id");
                    String paymentId = "payment.captured".equals(event) ? entity.optString("id") : null;
                    String status = "payment.captured".equals(event) ? "paid" : "failed";

                    mappingRepository.findByRazorpayOrderId(orderId).ifPresent(mapping ->
                            updatePaymentStatus(mapping.getOrderType(), mapping.getEntityId(), status, paymentId));
                }
            }
        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    private void updatePaymentStatus(String orderType, long entityId, String status, String paymentId) {
        if ("course".equalsIgnoreCase(orderType)) {
            coursePurchaseRepository.findById(entityId).ifPresent(p -> {
                p.setPaymentStatus(status);
                if (paymentId != null) p.setPaymentId(paymentId);
                coursePurchaseRepository.save(p);
            });
        } else if ("shop".equalsIgnoreCase(orderType)) {
            shopOrderRepository.findById(entityId).ifPresent(o -> {
                o.setPaymentStatus(status);
                if (paymentId != null) o.setPaymentId(paymentId);
                shopOrderRepository.save(o);
            });
        } else if ("booking".equalsIgnoreCase(orderType)) {
            bookingRepository.findById(entityId).ifPresent(booking -> {
                boolean wasAlreadyPaid = "paid".equalsIgnoreCase(booking.getPaymentStatus());
                booking.setPaymentStatus(status);
                if (paymentId != null) booking.setPaymentId(paymentId);
                bookingRepository.save(booking);
                if ("paid".equals(status) && !wasAlreadyPaid) {
                    brevoEmailService.sendBookingConfirmationToCustomer(booking);
                    brevoEmailService.sendBookingNotificationToAdmin(booking);
                }
            });
        }
    }
}
