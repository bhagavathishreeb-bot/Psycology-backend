package com.psycology.backend.dto;

/**
 * One-hour booking window in the business timezone.
 */
public class TimeSlotDto {

    /** Start time HH:mm (24h). */
    private String start;
    /** End time HH:mm (24h). */
    private String end;

    public TimeSlotDto() {}

    public TimeSlotDto(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }
    public String getEnd() { return end; }
    public void setEnd(String end) { this.end = end; }
}
