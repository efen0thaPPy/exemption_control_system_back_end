package com.efeakif.intibak_control.controller;

import com.efeakif.intibak_control.service.ErciyesCourseService;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.print.attribute.standard.Media;

import org.springframework.boot.actuate.web.mappings.MappingsEndpoint.ApplicationMappingsDescriptor;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties.Simple;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.dto.request.ApplicationRequestDto;
import com.efeakif.intibak_control.dto.request.SelectedCourseRequestDto;
import com.efeakif.intibak_control.dto.response.ApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.ApplicationUserResponseDto;
import com.efeakif.intibak_control.dto.response.SelectedCourseResponse;
import com.efeakif.intibak_control.dto.response.SimpleApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.ValidateResponseDto;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.mapping.ApplicationMapper;
import com.efeakif.intibak_control.service.ApplicationService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/applications")
@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("")
    public ResponseEntity<List<SimpleApplicationResponseDto>> getApplications(
            @RequestParam(required = false) ApplicationStatus applicationStatus) {

        List<SimpleApplicationResponseDto> applicationResponseDto = applicationService
                .getApplications(applicationStatus);
        return ResponseEntity.ok(applicationResponseDto);

    }

    @GetMapping("/{trackingcode}")
    public ResponseEntity<ApplicationResponseDto> getApplications(
            @PathVariable(name = "trackingcode") String trackingCode) {

        ApplicationResponseDto applicationResponseDto = applicationService.getApplication(trackingCode);

        return ResponseEntity.ok(applicationResponseDto);
    }

    @GetMapping(value = "/{trackingcode}/pdfs/transcripts", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> findTranscript(@PathVariable(name = "trackingcode") String trackingCode) {

        Resource resource = applicationService.getTranscript(trackingCode);

        return ResponseEntity.ok(resource);

    }

    @GetMapping(value = "/{trackingcode}/pdfs/coursecontents/{courseId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> findCourseContent(@PathVariable long courseId,
            @PathVariable(name = "trackingcode") String trackingCode) {

        Resource resource = applicationService.findCourseContentUrl(courseId, trackingCode);

        return ResponseEntity.ok(resource);

    }

    @PostMapping(value = "/submissions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApplicationUserResponseDto> submitApplication(
            @RequestPart ApplicationRequestDto applicationRequestDto, @RequestParam MultipartFile transcriptFile,

            // NOTE: courseFiles also has the transcriptFile but no need to change it for
            // now
            @RequestParam Map<String, MultipartFile> courseFiles) {

        applicationRequestDto.getTranscriptRequestDto().setTranscriptFile(transcriptFile);

        for (SelectedCourseRequestDto selectedCourseRequestDto : applicationRequestDto.getCourses()) {
            selectedCourseRequestDto.setCoursePdf(courseFiles.get(selectedCourseRequestDto.getCourseCode()));
        }

        ApplicationUserResponseDto applicationUserResponseDto = applicationService
                .submitApplication(applicationRequestDto);

        return ResponseEntity.ok(applicationUserResponseDto);

    }

    @PostMapping(value = "/validations", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ValidateResponseDto> validateApplication(
            @RequestPart @Valid ApplicationRequestDto applicationRequestDto, @RequestParam MultipartFile transcriptFile,
            @RequestParam Map<String, MultipartFile> courseFiles) {

        applicationRequestDto.getTranscriptRequestDto().setTranscriptFile(transcriptFile);

        for (SelectedCourseRequestDto selectedCourseRequestDto : applicationRequestDto.getCourses()) {
            selectedCourseRequestDto.setCoursePdf(courseFiles.get(selectedCourseRequestDto.getCourseCode()));
        }

        ValidateResponseDto validateResponseDto = applicationService
                .validateApplication(applicationRequestDto);

        return ResponseEntity.ok(validateResponseDto);

    }

    @PutMapping("/{trackingCode}")
    public ResponseEntity<ApplicationResponseDto> manageApplicationStatus(
            @RequestParam ApplicationStatus applicationStatus,
            @PathVariable String trackingCode) {

        ApplicationResponseDto applicationResponseDto = applicationService.manageApplicationStatus(applicationStatus,
                trackingCode);

        return ResponseEntity.ok(applicationResponseDto);

    }

    @PutMapping("/{trackingCode}/courses/{courseId}")
    public ResponseEntity<SelectedCourseResponse> manageCourseStatus(

            @RequestParam(required = false) String newCourseCode,
            @RequestParam(required = false) ExemptionStatus exemptionStatus,
            @PathVariable String trackingCode,
            @PathVariable long courseId) {

        SelectedCourseResponse selectedCourse = applicationService.manageCourseStatus(newCourseCode, exemptionStatus,
                trackingCode,
                courseId);

        return ResponseEntity.status(HttpStatus.CREATED).body(selectedCourse);

    }

    @DeleteMapping("/{trackingCode}/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(

            @PathVariable String trackingCode,
            @PathVariable long courseId) {

        applicationService.deleteCourse(trackingCode, courseId);

        return ResponseEntity.noContent().build();

    }

    @RequestBody(content = @Content(encoding = @Encoding(name = "selectedCourseRequestDto", contentType = MediaType.APPLICATION_JSON_VALUE)))
    @PostMapping(value = "/{trackingCode}/courses", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SelectedCourseResponse> addCourse(

            @RequestPart SelectedCourseRequestDto selectedCourseRequestDto,
            @RequestParam MultipartFile multipartFile,
            @PathVariable String trackingCode) {

        SelectedCourseResponse selectedCourseResponse = applicationService.addCourse(trackingCode,
                selectedCourseRequestDto, multipartFile);

        return ResponseEntity.status(HttpStatus.CREATED).body(selectedCourseResponse);

    }

}
