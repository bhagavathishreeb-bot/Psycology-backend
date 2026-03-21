package com.psycology.backend.controller;

import com.psycology.backend.dto.BookingRequest;
import com.psycology.backend.entity.Booking;
import com.psycology.backend.repository.BookingRepository;
import com.psycology.backend.service.BrevoEmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final BrevoEmailService brevoEmailService;

    public BookingController(BookingRepository bookingRepository, BrevoEmailService brevoEmailService) {
        this.bookingRepository = bookingRepository;
        this.brevoEmailService = brevoEmailService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = new Booking();
        booking.setName(request.getName());
        booking.setEmail(request.getEmail());
        booking.setAge(request.getAge());
        booking.setOccupation(request.getOccupation());
        booking.setPhone(request.getPhone());
        booking.setDob(request.getDob());
        booking.setGender(request.getGender());
        booking.setCity(request.getCity());
        booking.setPreferredLanguage(request.getPreferredLanguage());
        booking.setWhatBringsToTherapy(request.getWhatBringsToTherapy());
        booking.setHowLongConcerns(request.getHowLongConcerns());
        booking.setConcerns(request.getConcerns());
        booking.setOtherConcern(request.getOtherConcern());
        booking.setSeenPsychologistBefore(request.getSeenPsychologistBefore());
        booking.setPreviousDiagnosis(request.getPreviousDiagnosis());
        booking.setDiagnosisDuration(request.getDiagnosisDuration());
        booking.setSession(request.getSession());
        booking.setSessionDuration(request.getSessionDuration());
        booking.setSessionPrice(request.getSessionPrice());
        booking.setPaymentStatus("pending");

        booking = bookingRepository.save(booking);

        // Send confirmation email to customer
        brevoEmailService.sendBookingConfirmationToCustomer(booking);
        // Send notification to admin
        brevoEmailService.sendBookingNotificationToAdmin(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", booking.getId()));
    }
}
