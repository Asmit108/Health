package com.health.check.Controller;

import com.health.check.Dto.SymptomRequestDto;
import com.health.check.Dto.SymptomResponseDto;
import com.health.check.Service.GenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/symptoms")
public class GenAIController {

    @Autowired
    private GenAIService genAIService;

    @PostMapping("/check")
    public ResponseEntity<SymptomResponseDto> check(@RequestBody SymptomRequestDto request) {
        return ResponseEntity.ok(genAIService.checkSymptoms(request.getSymptoms()));
    }
}
