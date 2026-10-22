package com.efeakif.intibak_control.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.service.AiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    @PostMapping(value = "/coursecontent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseContentDto> analyzeCourseContents(@RequestParam MultipartFile courseContent)
            throws IOException {

        if (courseContent.isEmpty())
            throw new IllegalArgumentException("courseContent pdf cannot be empty");

        CourseContentDto courseContentDto = aiService.analyzeCourseContents(courseContent);

        return ResponseEntity.ok(courseContentDto);

    }

    @PostMapping(value = "/transcript", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptDto> analyzeTranscription(@RequestParam MultipartFile transcript)
            throws IOException {

        if (transcript.isEmpty())
            throw new IllegalArgumentException("transcript pdf cannot be empty");

        TranscriptDto transcriptDto = aiService.analyzeTranscript(transcript);

        return ResponseEntity.ok(transcriptDto);

    }

}
