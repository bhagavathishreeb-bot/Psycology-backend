package com.psycology.backend.controller;

import com.psycology.backend.dto.BookingRequest;
import com.psycology.backend.entity.Booking;
import com.psycology.backend.repository.BookingRepository;
import com.psycology.backend.service.BookingAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final DateTimeFormatter SLOT_HM = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingRepository bookingRepository;
    private final BookingAvailabilityService bookingAvailabilityService;

    public BookingController(BookingRepository bookingRepository,
                             BookingAvailabilityService bookingAvailabilityService) {
        this.bookingRepository = bookingRepository;
        this.bookingAvailabilityService = bookingAvailabilityService;
    }

    /**
     * Available one-hour slots for a date. Empty when weekend or festival day.
     * Slots 9:00–20:00 in configured timezone, excluding 14:00–16:00 by default.
     */
    @GetMapping("/available-slots")
    public ResponseEntity<?> availableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(bookingAvailabilityService.getSlotsForDate(date));
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        LocalTime slotStart;
        try {
            slotStart = LocalTime.parse(request.getAppointmentSlotStart(), SLOT_HM);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid appointment slot time; use HH:mm"));
        }
        bookingAvailabilityService.assertAppointmentBookable(request.getAppointmentDate(), slotStart);

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
        booking.setAppointmentDate(request.getAppointmentDate());
        booking.setAppointmentSlotStart(slotStart);
        booking.setPaymentStatus("pending");

        booking = bookingRepository.save(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", booking.getId()));
    }
}
