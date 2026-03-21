package com.psycology.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String occupation;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String dob;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String preferredLanguage;

    @Column(nullable = false, length = 2000)
    private String whatBringsToTherapy;

    @Column(nullable = false)
    private String howLongConcerns;

    @Convert(converter = com.psycology.backend.config.ConcernsConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Boolean> concerns;

    private String otherConcern;

    private String seenPsychologistBefore;

    private String previousDiagnosis;

    private String diagnosisDuration;

    @Column(nullable = false)
    private String session;

    @Column(nullable = false)
    private String sessionDuration;

    @Column(nullable = false)
    private Double sessionPrice;

    @Column(nullable = false)
    private String paymentStatus = "pending";

    private String paymentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}
