package com.psycology.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "booking.availability")
public class BookingAvailabilityProperties {

    /** IANA timezone for weekday/weekend rules (e.g. Asia/Kolkata). */
    private String timezone = "Asia/Kolkata";

    /**
     * ISO dates (yyyy-MM-dd) treated as closed days (festivals / holidays).
     */
    private List<String> festivalDates = new ArrayList<>();

    /** Hour (0–23) when the first slot starts. */
    private int firstSlotHour = 9;

    /**
     * Hour when the working day ends; last slot is (lastSlotEndHour - 1)–lastSlotEndHour.
     * E.g. 20 → last start at 19:00, slot 19:00–20:00.
     */
    private int lastSlotEndHour = 20;

    /**
     * Slot start hours to omit (e.g. 14 and 15 for a 2pm–4pm block).
     */
    private List<Integer> excludedSlotStartHours = List.of(14, 15);

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public List<String> getFestivalDates() { return festivalDates; }
    public void setFestivalDates(List<String> festivalDates) { this.festivalDates = festivalDates; }
    public int getFirstSlotHour() { return firstSlotHour; }
    public void setFirstSlotHour(int firstSlotHour) { this.firstSlotHour = firstSlotHour; }
    public int getLastSlotEndHour() { return lastSlotEndHour; }
    public void setLastSlotEndHour(int lastSlotEndHour) { this.lastSlotEndHour = lastSlotEndHour; }
    public List<Integer> getExcludedSlotStartHours() { return excludedSlotStartHours; }
    public void setExcludedSlotStartHours(List<Integer> excludedSlotStartHours) {
        this.excludedSlotStartHours = excludedSlotStartHours;
    }
}
