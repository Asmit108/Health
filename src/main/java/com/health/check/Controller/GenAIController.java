package com.health.check.Controller;

import com.health.check.Dto.PatientProfileResponse;
import com.health.check.Dto.SymptomRequestDto;
import com.health.check.Dto.SymptomResponseDto;
import com.health.check.Service.GenAIService;
import com.health.check.Service.PatientService;
import com.health.check.Service.SymptomService;
import com.health.check.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private GenAIService genAIService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private SymptomService symptomService;

    @Operation(summary = "Check Symptoms")
    @PreAuthorize("hasRole('PATIENT')")
    @PostMapping("/check")
    public ResponseEntity<?> check(@Valid @RequestBody SymptomRequestDto request) throws NotFoundException {
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
