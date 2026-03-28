package com.psycology.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Map;

public class BookingRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Age is required")
    @Min(1) @Max(120)
    private Integer age;

    @NotBlank(message = "Occupation is required")
    private String occupation;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Date of birth is required")
    private String dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Preferred language is required")
    private String preferredLanguage;

    @NotBlank(message = "What brings you to therapy is required")
    private String whatBringsToTherapy;

    @NotBlank(message = "How long have you had these concerns is required")
    private String howLongConcerns;

    private Map<String, Boolean> concerns;
    private String otherConcern;
    private String seenPsychologistBefore;
    private String previousDiagnosis;
    private String diagnosisDuration;

    @NotBlank(message = "Session type is required")
    private String session;

    @NotBlank(message = "Session duration is required")
    private String sessionDuration;

    @NotNull(message = "Session price is required")
    @Min(0)
    private Double sessionPrice;

    @NotNull(message = "Appointment date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotBlank(message = "Appointment slot start is required (HH:mm, e.g. 09:00)")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Use 24h HH:mm (e.g. 09:00)")
    private String appointmentSlotStart;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public String getWhatBringsToTherapy() { return whatBringsToTherapy; }
    public void setWhatBringsToTherapy(String whatBringsToTherapy) { this.whatBringsToTherapy = whatBringsToTherapy; }
    public String getHowLongConcerns() { return howLongConcerns; }
    public void setHowLongConcerns(String howLongConcerns) { this.howLongConcerns = howLongConcerns; }
    public Map<String, Boolean> getConcerns() { return concerns; }
    public void setConcerns(Map<String, Boolean> concerns) { this.concerns = concerns; }
    public String getOtherConcern() { return otherConcern; }
    public void setOtherConcern(String otherConcern) { this.otherConcern = otherConcern; }
    public String getSeenPsychologistBefore() { return seenPsychologistBefore; }
    public void setSeenPsychologistBefore(String seenPsychologistBefore) { this.seenPsychologistBefore = seenPsychologistBefore; }
    public String getPreviousDiagnosis() { return previousDiagnosis; }
    public void setPreviousDiagnosis(String previousDiagnosis) { this.previousDiagnosis = previousDiagnosis; }
    public String getDiagnosisDuration() { return diagnosisDuration; }
    public void setDiagnosisDuration(String diagnosisDuration) { this.diagnosisDuration = diagnosisDuration; }
    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }
    public String getSessionDuration() { return sessionDuration; }
    public void setSessionDuration(String sessionDuration) { this.sessionDuration = sessionDuration; }
    public Double getSessionPrice() { return sessionPrice; }
    public void setSessionPrice(Double sessionPrice) { this.sessionPrice = sessionPrice; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }
    public String getAppointmentSlotStart() { return appointmentSlotStart; }
    public void setAppointmentSlotStart(String appointmentSlotStart) {
        this.appointmentSlotStart = appointmentSlotStart;
    }
}
