package com.health.check.controller;

import com.health.check.dto.PatientProfileResponse;
import com.health.check.dto.SymptomRequestDto;
import com.health.check.dto.SymptomResponseDto;
import com.health.check.service.GenAIService;
import com.health.check.service.PatientService;
import com.health.check.service.SymptomService;
import com.health.check.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/symptoms")
public class GenAIController {

    private final GenAIService genAIService;

    private final PatientService patientService;

    private final SymptomService symptomService;

    public GenAIController(GenAIService genAIService, PatientService patientService, SymptomService symptomService) {
        this.genAIService = genAIService;
        this.patientService = patientService;
        this.symptomService = symptomService;
    }

    @Operation(summary = "Check Symptoms")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/check")
    public ResponseEntity<SymptomResponseDto> check(@Valid @RequestBody SymptomRequestDto request) throws NotFoundException {
        // Extract email from authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Get patient profile
        PatientProfileResponse patientProfileResponse = patientService.getPatientByEmail(email);

        // Call AI service to analyze symptoms
        SymptomResponseDto responseDto = genAIService.checkSymptoms(request.getSymptoms());

        // Store symptom analysis in database
        symptomService.createSymptoms(patientProfileResponse.getPatient(), responseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
