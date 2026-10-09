package com.efeakif.intibak_control.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import com.efeakif.intibak_control.service.seeding.CourseSeederService;
import com.efeakif.intibak_control.service.seeding.ErciyesScrapingSeedingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseSeederController {

    private final CourseSeederService courseSeederService;

    @Value("${erciyesUrl}")
    private String url;

    @PostMapping("/seed")
    public ResponseEntity<?> seedErciyesCourses(@RequestBody String academicYear) {

        courseSeederService.seed(academicYear);

        return ResponseEntity.ok("Succesfuly scraped and seeded from the url" + url);

    }

}
