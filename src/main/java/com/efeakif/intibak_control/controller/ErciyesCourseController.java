package com.efeakif.intibak_control.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efeakif.intibak_control.dto.response.ErciyesCourseResponseDto;
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.enums.TransferType;
import com.efeakif.intibak_control.mapping.Mapper;
import com.efeakif.intibak_control.repository.ErciyesCourseRepo;
import com.efeakif.intibak_control.service.ErciyesCourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/erciyes")
@RequiredArgsConstructor
public class ErciyesCourseController {

    private final ErciyesCourseService erciyesCourseService;

    @GetMapping("/courses")
    public ResponseEntity<List<ErciyesCourseResponseDto>> getCourses(TransferType transferType) {
        List<ErciyesCourseResponseDto> erciyesCourses = erciyesCourseService.findByMaxAcademicYear(transferType);
        return ResponseEntity.ok(erciyesCourses);

    }

    @GetMapping("/academic-years")
    public ResponseEntity<String> getMaxAcademicYear() {
        String maxYear = erciyesCourseService.findMaxAcademicYear();
        return ResponseEntity.ok(maxYear);

    }

    @GetMapping("/academic-years/{academicYear}")
    public ResponseEntity<List<ErciyesCourseResponseDto>> findByAcademicYear(@PathVariable String academicYear) {
        List<ErciyesCourseResponseDto> erciyesCourseResponseDtos = erciyesCourseService
                .findByAcademicYear(academicYear);
        return ResponseEntity.ok(erciyesCourseResponseDtos);

    }

}
