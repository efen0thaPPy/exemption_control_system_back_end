package com.efeakif.intibak_control.controller;

import org.hibernate.annotations.Fetch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.efeakif.intibak_control.service.ErciyesScrapingService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseSeedeController {

    private final ErciyesScrapingService erciyesScrapingService;

    @Value("${erciyesUrl}")
    private String url;

    @PostMapping("/seed")
    public ResponseEntity<?> seedErciyesCourses(@RequestBody String academicYear) {

        erciyesScrapingService.scrapeAndSeedtheCirriculum(url, academicYear);

        return ResponseEntity.ok("Succesfuly scraped and seeded from the url" + url);

    }

}
