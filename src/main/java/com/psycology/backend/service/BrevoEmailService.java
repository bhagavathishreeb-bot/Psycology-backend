package com.psycology.backend.service;

import com.psycology.backend.config.BrevoProperties;
import com.psycology.backend.entity.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);

    private final BrevoProperties brevoProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public BrevoEmailService(BrevoProperties brevoProperties) {
        this.brevoProperties = brevoProperties;
    }

    public void sendBookingConfirmationToCustomer(Booking booking) {
        String subject = "Your Counseling Session Booking Confirmation - ManoTaranga";
        String htmlContent = buildCustomerConfirmationHtml(booking);
        sendEmail(booking.getEmail(), booking.getName(), subject, htmlContent);
    }

    public void sendBookingNotificationToAdmin(Booking booking) {
        String adminEmail = brevoProperties.getAdminEmail();
        if (adminEmail == null || adminEmail.isBlank()) {
            log.warn("Brevo admin email not configured. Skipping admin notification.");
            return;
        }
        String subject = "New Counseling Session Booking #" + booking.getId() + " - ManoTaranga";
        String htmlContent = buildAdminNotificationHtml(booking);
        sendEmail(adminEmail, "Admin", subject, htmlContent);
    }

    private String buildCustomerConfirmationHtml(Booking booking) {
        return """
            <html>
            <head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;} .container{max-width:600px;margin:0 auto;padding:20px;} .header{background:#4a90a4;color:white;padding:15px;text-align:center;} .content{padding:20px;background:#f9f9f9;} .footer{text-align:center;padding:10px;font-size:12px;color:#666;}</style></head>
            <body>
            <div class="container">
            <div class="header"><h2>ManoTaranga - Counseling Session Confirmed</h2></div>
            <div class="content">
            <p>Dear %s,</p>
            <p>Thank you for booking a counseling session with ManoTaranga. Your booking has been confirmed.</p>
            <p><strong>Booking Details:</strong></p>
            <ul>
            <li>Session: %s</li>
            <li>Duration: %s</li>
            <li>Amount: ₹%.2f</li>
            </ul>
            <p>We will contact you shortly at %s to schedule your session.</p>
            <p>Take care of your mental well-being.</p>
            </div>
            <div class="footer">ManoTaranga - Your mental wellness partner</div>
            </div>
            </body>
            </html>
            """.formatted(
                booking.getName(),
                booking.getSession(),
                booking.getSessionDuration(),
                booking.getSessionPrice(),
                booking.getPhone()
            );
    }

    private String buildAdminNotificationHtml(Booking booking) {
        StringBuilder concernsStr = new StringBuilder();
        if (booking.getConcerns() != null && !booking.getConcerns().isEmpty()) {
            booking.getConcerns().forEach((k, v) -> {
                if (Boolean.TRUE.equals(v)) concernsStr.append(k).append(", ");
            });
        }
        if (concernsStr.length() > 0) concernsStr.setLength(concernsStr.length() - 2);

        return """
            <html>
            <head><style>body{font-family:Arial,sans-serif;line-height:1.6;color:#333;} .container{max-width:600px;margin:0 auto;padding:20px;} table{width:100%%;border-collapse:collapse;} td{padding:8px;border:1px solid #ddd;}</style></head>
            <body>
            <div class="container">
            <h2>New Counseling Session Booking #%d</h2>
            <table>
            <tr><td><strong>Name</strong></td><td>%s</td></tr>
            <tr><td><strong>Email</strong></td><td>%s</td></tr>
            <tr><td><strong>Phone</strong></td><td>%s</td></tr>
            <tr><td><strong>Age</strong></td><td>%d</td></tr>
            <tr><td><strong>Occupation</strong></td><td>%s</td></tr>
            <tr><td><strong>DOB</strong></td><td>%s</td></tr>
            <tr><td><strong>Gender</strong></td><td>%s</td></tr>
            <tr><td><strong>City</strong></td><td>%s</td></tr>
            <tr><td><strong>Preferred Language</strong></td><td>%s</td></tr>
            <tr><td><strong>What brings to therapy</strong></td><td>%s</td></tr>
            <tr><td><strong>How long concerns</strong></td><td>%s</td></tr>
            <tr><td><strong>Concerns</strong></td><td>%s</td></tr>
            <tr><td><strong>Other concern</strong></td><td>%s</td></tr>
            <tr><td><strong>Seen psychologist before</strong></td><td>%s</td></tr>
            <tr><td><strong>Previous diagnosis</strong></td><td>%s</td></tr>
            <tr><td><strong>Session</strong></td><td>%s</td></tr>
            <tr><td><strong>Duration</strong></td><td>%s</td></tr>
            <tr><td><strong>Price</strong></td><td>₹%.2f</td></tr>
            </table>
            </div>
            </body>
            </html>
            """.formatted(
                booking.getId(),
                booking.getName(),
                booking.getEmail(),
                booking.getPhone(),
                booking.getAge(),
                booking.getOccupation(),
                booking.getDob(),
                booking.getGender(),
                booking.getCity(),
                booking.getPreferredLanguage(),
                booking.getWhatBringsToTherapy(),
                booking.getHowLongConcerns(),
                concernsStr.toString(),
                nullToEmpty(booking.getOtherConcern()),
                nullToEmpty(booking.getSeenPsychologistBefore()),
                nullToEmpty(booking.getPreviousDiagnosis()),
                booking.getSession(),
                booking.getSessionDuration(),
                booking.getSessionPrice()
            );
    }

    private String nullToEmpty(String s) {
        return s != null ? s : "";
    }

    private void sendEmail(String toEmail, String toName, String subject, String htmlContent) {
        if (toEmail == null || toEmail.isBlank()) {
            log.warn("Cannot send email: recipient email is null or empty");
            return;
        }
        if (brevoProperties.getApiKey() == null || brevoProperties.getApiKey().isBlank()) {
            log.warn("Brevo API key not configured. Skipping email to {}", toEmail);
            return;
        }

        String fromName = brevoProperties.getFromName() != null ? brevoProperties.getFromName() : "ManoTaranga";
        String fromEmail = brevoProperties.getFromEmail() != null ? brevoProperties.getFromEmail() : "noreply@manotaranga.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoProperties.getApiKey());

        Map<String, Object> body = Map.of(
            "sender", Map.of("name", fromName, "email", fromEmail),
            "to", List.of(Map.of("email", toEmail, "name", toName != null ? toName : "")),
            "subject", subject != null ? subject : "",
            "htmlContent", htmlContent != null ? htmlContent : ""
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(BREVO_API_URL, HttpMethod.POST, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email sent successfully to {}", toEmail);
            } else {
                log.error("Brevo API error: {} - {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
