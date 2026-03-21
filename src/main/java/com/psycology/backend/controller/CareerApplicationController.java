package com.psycology.backend.controller;

import com.psycology.backend.entity.CareerApplication;
import com.psycology.backend.repository.CareerApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/career-applications")
public class CareerApplicationController {

    private static final String UPLOAD_DIR = "uploads/resumes";

    private final CareerApplicationRepository careerApplicationRepository;

    public CareerApplicationController(CareerApplicationRepository careerApplicationRepository) {
        this.careerApplicationRepository = careerApplicationRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createCareerApplication(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam(value = "linkedin", required = false) String linkedin,
            @RequestParam("experience") String experience,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("jobTitle") String jobTitle,
            @RequestParam("jobType") String jobType,
            @RequestParam("jobLocation") String jobLocation) {

        if (name == null || name.isBlank() || email == null || email.isBlank() ||
                phone == null || phone.isBlank() || experience == null || experience.isBlank() ||
                resume == null || resume.isEmpty() || jobTitle == null || jobTitle.isBlank() ||
                jobType == null || jobType.isBlank() || jobLocation == null || jobLocation.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Required fields: name, email, phone, experience, resume, jobTitle, jobType, jobLocation"));
        }

        String resumePath;
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFilename = resume.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(filename);
            resume.transferTo(filePath.toFile());
            resumePath = UPLOAD_DIR + "/" + filename;
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save resume: " + e.getMessage()));
        }

        CareerApplication application = new CareerApplication();
        application.setName(name.trim());
        application.setEmail(email.trim());
        application.setPhone(phone.trim());
        application.setLinkedin(linkedin != null ? linkedin.trim() : null);
        application.setExperience(experience.trim());
        application.setMessage(message != null ? message.trim() : null);
        application.setResumePath(resumePath);
        application.setJobTitle(jobTitle.trim());
        application.setJobType(jobType.trim());
        application.setJobLocation(jobLocation.trim());

        application = careerApplicationRepository.save(application);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", application.getId()));
    }
}
