package com.psycology.backend.service;

import com.psycology.backend.config.BookingAvailabilityProperties;
import com.psycology.backend.dto.AvailableSlotsResponse;
import com.psycology.backend.dto.TimeSlotDto;
import com.psycology.backend.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BookingAvailabilityService {

    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
    private static final String PAYMENT_FAILED = "failed";

    private final BookingAvailabilityProperties properties;
    private final BookingRepository bookingRepository;

    public BookingAvailabilityService(BookingAvailabilityProperties properties,
                                      BookingRepository bookingRepository) {
        this.properties = properties;
        this.bookingRepository = bookingRepository;
    }

    public AvailableSlotsResponse getSlotsForDate(LocalDate date) {
        String dateStr = date.toString();

        if (isClosedDay(date)) {
            ZoneId zone = ZoneId.of(properties.getTimezone());
            DayOfWeek dow = date.atStartOfDay(zone).getDayOfWeek();
            if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                return AvailableSlotsResponse.blocked(dateStr, "WEEKEND");
            }
            return AvailableSlotsResponse.blocked(dateStr, "FESTIVAL");
        }

        AvailableSlotsResponse response = new AvailableSlotsResponse();
        response.setDate(dateStr);
        response.setAvailable(true);
        response.setUnavailableReason(null);
        response.setSlots(buildOpenSlotsForDate(date));
        return response;
    }

    /**
     * Validates date and slot before persisting a booking. Frees slot when paymentStatus becomes {@code failed}.
     */
    public void assertAppointmentBookable(LocalDate date, LocalTime slotStart) {
        if (isClosedDay(date)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bookings are not available on this date.");
        }
        if (!isOfferedSlot(slotStart)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or unavailable time slot.");
        }
        if (bookingRepository.existsByAppointmentDateAndAppointmentSlotStartAndPaymentStatusNot(
                date, slotStart, PAYMENT_FAILED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This time slot is already booked.");
        }
    }

    private boolean isClosedDay(LocalDate date) {
        ZoneId zone = ZoneId.of(properties.getTimezone());
        DayOfWeek dow = date.atStartOfDay(zone).getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return true;
        }
        return isFestivalDate(date);
    }

    private boolean isFestivalDate(LocalDate date) {
        for (String raw : properties.getFestivalDates()) {
            if (raw == null || raw.isBlank()) {
                continue;
            }
            try {
                if (LocalDate.parse(raw.trim()).equals(date)) {
                    return true;
                }
            } catch (Exception ignored) {
                // skip malformed entries
            }
        }
        return false;
    }

    private boolean isOfferedSlot(LocalTime slotStart) {
        for (TimeSlotDto s : buildHourlySlots()) {
            if (LocalTime.parse(s.getStart(), HM).equals(slotStart)) {
                return true;
            }
        }
        return false;
    }

    private List<TimeSlotDto> buildOpenSlotsForDate(LocalDate date) {
        Set<LocalTime> taken = new HashSet<>(bookingRepository.findTakenSlotStartsOn(date, PAYMENT_FAILED));
        List<TimeSlotDto> open = new ArrayList<>();
        for (TimeSlotDto s : buildHourlySlots()) {
            LocalTime start = LocalTime.parse(s.getStart(), HM);
            if (!taken.contains(start)) {
                open.add(s);
            }
        }
        return open;
    }

    private List<TimeSlotDto> buildHourlySlots() {
        int first = properties.getFirstSlotHour();
        int lastEnd = properties.getLastSlotEndHour();
        Set<Integer> excluded = new HashSet<>(properties.getExcludedSlotStartHours());

        List<TimeSlotDto> slots = new ArrayList<>();
        for (int h = first; h < lastEnd; h++) {
            if (excluded.contains(h)) {
                continue;
            }
            String start = HM.format(LocalTime.of(h, 0));
            String end = HM.format(LocalTime.of(h + 1, 0));
            slots.add(new TimeSlotDto(start, end));
        }
        return slots;
    }
}
