package com.efeakif.intibak_control.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.service.ExemptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exemption")
public class ExemptionController {
    private final ExemptionService exemptionService;

    @PostMapping(value = "analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ExemptionResponseDto>> analyzeExemeption(@RequestParam MultipartFile transcript,
            @RequestParam MultipartFile courseContent) throws IOException {

        if (transcript.isEmpty() || courseContent.isEmpty())
            throw new IllegalArgumentException("transcript or course content pdfs cannot be empty");

        List<ExemptionResponseDto> exemptionResponseDtos = exemptionService.analyzeExemption(transcript,
                courseContent);
        return ResponseEntity.ok(exemptionResponseDtos);

    }

}