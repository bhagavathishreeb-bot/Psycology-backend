package com.psycology.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AvailableSlotsResponse {

    /** Requested date (yyyy-MM-dd). */
    private String date;
    private boolean available;
    /** When available is false: WEEKEND, FESTIVAL, or null if not applicable. */
    private String unavailableReason;
    private List<TimeSlotDto> slots = new ArrayList<>();

    public AvailableSlotsResponse() {}

    public static AvailableSlotsResponse blocked(String date, String reason) {
        var r = new AvailableSlotsResponse();
        r.setDate(date);
        r.setAvailable(false);
        r.setUnavailableReason(reason);
        r.setSlots(List.of());
        return r;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getUnavailableReason() { return unavailableReason; }
    public void setUnavailableReason(String unavailableReason) { this.unavailableReason = unavailableReason; }
    public List<TimeSlotDto> getSlots() { return slots; }
    public void setSlots(List<TimeSlotDto> slots) { this.slots = slots; }
}
