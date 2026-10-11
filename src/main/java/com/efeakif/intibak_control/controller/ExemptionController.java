package com.efeakif.intibak_control.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;

import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.dto.request.ExemptionAnalysisRequestDto;
import com.efeakif.intibak_control.dto.request.TranscriptRequestDto;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.service.ExemptionService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exemption")
public class ExemptionController {
    private final ExemptionService exemptionService;

    // @PostMapping(value = "analysis", consumes =
    // MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<List<ExemptionResponseDto>>
    // analyzeExemeption(@RequestParam MultipartFile transcript,
    // @RequestParam MultipartFile courseContent) throws IOException {

    // if (transcript.isEmpty() || courseContent.isEmpty())
    // throw new IllegalArgumentException("transcript or course content pdfs cannot
    // be empty");

    // List<ExemptionResponseDto> exemptionResponseDtos =
    // exemptionService.analyzeExemption(transcript,
    // courseContent);
    // return ResponseEntity.ok(exemptionResponseDtos);

    // }

    @PostMapping(value = "/transcript", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptDto> analyzeTranscription(@RequestParam MultipartFile transcript)
            throws IOException {

        if (transcript.isEmpty())
            throw new IllegalArgumentException("transcript pdf cannot be empty");

        TranscriptDto transcriptDto = exemptionService.analyzeTranscript(transcript);

        return ResponseEntity.ok(transcriptDto);

    }

    @PostMapping(value = "/coursecontent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseContentDto> analyzeCourseContents(@RequestParam MultipartFile courseContent)
            throws IOException {

        if (courseContent.isEmpty())
            throw new IllegalArgumentException("courseContent pdf cannot be empty");

        CourseContentDto courseContentDto = exemptionService.analyzeCourseContents(courseContent);

        return ResponseEntity.ok(courseContentDto);

    }

    @PostMapping("/exemption")
    public ResponseEntity<List<ExemptionResponseDto>> exemptCourse(
            @RequestBody ExemptionAnalysisRequestDto exemptionAnalysisRequestDto)
            throws IOException {

        CourseContentDto courseContentDto = exemptionAnalysisRequestDto.getCourseContentDto();
        TranscriptRequestDto transcriptRequestDto = exemptionAnalysisRequestDto.getTranscriptRequestDto();

        // String transcriptCourseCode = transcriptRequestDto.getCourseCode()
        // .toLowerCase(Locale.forLanguageTag("tr")).trim();
        // String transcriptCourseName = transcriptRequestDto.getCourseName()
        // .toLowerCase(Locale.forLanguageTag("tr")).trim();

        // String contentCourseCode =
        // courseContentDto.getCourseCode().toLowerCase(Locale.forLanguageTag("tr"))
        // .trim();
        // String contentCourseName =
        // courseContentDto.getCourseName().toLowerCase(Locale.forLanguageTag("tr"))
        // .trim();

        // if (!transcriptCourseCode.equals(contentCourseCode) ||
        // !transcriptCourseName.equals(contentCourseName))
        // throw new IOException("course codes and names have to match");

        List<ExemptionResponseDto> exemptionResponseDto = exemptionService.exemptCourse(transcriptRequestDto,
                courseContentDto);

        return ResponseEntity.ok(exemptionResponseDto);

    }

}