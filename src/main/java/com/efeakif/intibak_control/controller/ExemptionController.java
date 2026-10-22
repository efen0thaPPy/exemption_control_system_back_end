package com.efeakif.intibak_control.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.service.ExemptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exemptions")
public class ExemptionController {
    private final ExemptionService exemptionService;

    @PostMapping("/exemption")
    public ResponseEntity<List<ExemptionResponseDto>> exemptCourse(
            @RequestParam String trackingCode, @RequestParam String courseCode)
            throws IOException {

        List<ExemptionResponseDto> exemptionResponseDto = exemptionService.exemptCourse(trackingCode, courseCode);

        return ResponseEntity.ok(exemptionResponseDto);

    }

}