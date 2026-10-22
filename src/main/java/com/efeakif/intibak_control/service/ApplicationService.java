package com.efeakif.intibak_control.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import javax.naming.NameNotFoundException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.internal.StudentCourseDto;
import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.dto.request.ApplicationRequestDto;
import com.efeakif.intibak_control.dto.request.SelectedCourseRequestDto;
import com.efeakif.intibak_control.dto.request.TranscriptRequestDto;
import com.efeakif.intibak_control.dto.response.ApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.ApplicationUserResponseDto;
import com.efeakif.intibak_control.dto.response.SimpleApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.ValidateResponseDto;
import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;
import com.efeakif.intibak_control.enums.AiModel;
import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.TransferType;
import com.efeakif.intibak_control.exception.ResourceNotFoundException;
import com.efeakif.intibak_control.mapping.ApplicationMapper;
import com.efeakif.intibak_control.mapping.Mapper;
import com.efeakif.intibak_control.repository.ApplicationRepo;
import com.efeakif.intibak_control.repository.ErciyesCourseRepo;
import com.efeakif.intibak_control.service.ai.LLMService;
import com.efeakif.intibak_control.service.document.FileStorageService;
import com.efeakif.intibak_control.service.document.PdfExtractionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {
   private final Mapper mapper;
   private final FileStorageService fileStorageService;
   private final ApplicationRepo applicationRepo;
   private final ErciyesCourseRepo erciyesRepo;
   private final ApplicationMapper applicationMapper;

   @Transactional
   public ValidateResponseDto validateApplication(ApplicationRequestDto applicationRequestDto) {

      ValidateResponseDto validateResponseDto = new ValidateResponseDto();

      int year = LocalDate.now().getYear();

      TransferType transferType = applicationRequestDto.getTransferType();
      List<SelectedCourseRequestDto> courses = applicationRequestDto.getCourses();

      for (SelectedCourseRequestDto course : courses) {

         if (!course.getGrade().isPassing()) {
            validateResponseDto.addError("you cant exempt a course that you failed" + course.getCourseName());
         }

         String[] splitBySpaces = course.getSemester().split(" ");
         String[] splitByDashes = splitBySpaces[0].split("-");
         int issuedSemester = Integer.parseInt(splitByDashes[0]);
         if (year - issuedSemester > 5 && course.getCourseType() != CourseType.COMMON_MANDATORY) {
            validateResponseDto.addError((String.format(
                  "The year difference between today and the year you passed the not-common-mandatory-course (%s) in is more than 5 you cannot exempt such courses.",
                  course.getCourseName())));

         }
         ErciyesCourse erciyesCourse = erciyesRepo.findByCourseCodeandMaxAcademicYear(course.getTargetCourseCode());

         if (erciyesCourse == null) {
            validateResponseDto.addError(
                  String.format("Target course couldnt be found with the code %s", course.getTargetCourseCode()));
         }
         if (course.getEcts() < erciyesCourse.getEcts()
               && CourseType.COMMON_MANDATORY != course.getCourseType()) {
            validateResponseDto.addWarning(
                  String.format("The ects your course has is lower than of the target course %s",
                        erciyesCourse.getCourseName()));
         }

         if (transferType == TransferType.VERTICAL_TRANSFER && erciyesCourse.getSemester() > 4) {

            validateResponseDto.addError(
                  String.format(
                        "The course you want your course exempted with (%s) is not in the first 4 semester (%s. semester) ",
                        erciyesCourse.getCourseName(), erciyesCourse.getSemester()));

         }

      }
      return validateResponseDto;

   }

   @Transactional
   public ApplicationUserResponseDto submitApplication(ApplicationRequestDto applicationRequestDto) {

      ValidateResponseDto validateResponseDto = validateApplication(applicationRequestDto);
      if (!validateResponseDto.getErrors().isEmpty() || !validateResponseDto.isValid()) {

         throw new IllegalArgumentException(String.join("; ", validateResponseDto.getErrors()));
      }

      String transcriptPdfPath = null;
      String trackingCode = "APP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

      TranscriptRequestDto transcriptRequestDto = applicationRequestDto.getTranscriptRequestDto();
      TransferType transferType = applicationRequestDto.getTransferType();
      List<SelectedCourseRequestDto> courses = applicationRequestDto.getCourses();

      MultipartFile transcriptFile = transcriptRequestDto.getTranscriptFile();

      if (transcriptFile != null && !transcriptFile.isEmpty()) {
         transcriptPdfPath = fileStorageService.saveFile(transcriptFile, trackingCode, "transcript");

      } else
         throw new RuntimeException("file must be provided");

      Student student = mapper.toEntity(transcriptRequestDto);

      Application application = new Application();
      application.setTrackingCode(trackingCode);
      application.setApplicationStatus(ApplicationStatus.PENDING);
      application.setTranscriptPdfPath(transcriptPdfPath);
      application.setTransferType(transferType);
      application.setStudent(student);

      List<SelectedCourse> selectedCourses = new ArrayList<>();

      for (SelectedCourseRequestDto selectedCourseRequestDto : courses) {

         String suffix = selectedCourseRequestDto.getCourseName().replaceAll("[^a-zA-Z0-9.-]", "_");

         String coursePdfPath = fileStorageService.saveFile(selectedCourseRequestDto.getCoursePdf(), trackingCode,
               suffix);
         SelectedCourse selectedCourse = mapper.toEntity(selectedCourseRequestDto, coursePdfPath);
         selectedCourses.add(selectedCourse);

      }
      application.setSelectedCourses(selectedCourses);
      applicationRepo.save(application);

      return new ApplicationUserResponseDto(trackingCode,
            "Your request has been sent you can check with your request using tracking code: " + trackingCode);

   }

   public List<SimpleApplicationResponseDto> getApplications(ApplicationStatus applicationStatus) {

      return (applicationStatus == null)
            ? (applicationRepo.findAll().stream().map(x -> applicationMapper.toSimpleDto(x)).toList())
            : (applicationRepo.findByApplicationStatus(applicationStatus).stream()
                  .map(x -> applicationMapper.toSimpleDto(x))
                  .toList());

   }

   public ApplicationResponseDto getApplication(String trackingCode) {

      Application application = applicationRepo.findByTrackingCode(trackingCode);

      return applicationMapper.toDto(application);

   }

   public Resource findTranscriptUrl(String trackingCode) {

      String path = applicationRepo.findTranscriptPdfPathByTrackingCode(trackingCode);

      if (path == null || path.isBlank())
         throw new ResourceNotFoundException("url doesnt exist");

      return fileStorageService.loadFile(path);

   }

   public Resource findCourseContentUrl(String courseCode, String trackingCode) {

      String path = applicationRepo.findCourseContentPdfByTrackingCodeAndCourseCode(courseCode, trackingCode);

      if (path == null || path.isBlank())
         throw new ResourceNotFoundException("url doesnt exist");

      return fileStorageService.loadFile(path);

   }

   public StudentCourseDto findCourse(String trackingCode, String courseCode) {

      SelectedCourse selectedCourse = applicationRepo.findSelectedCourseByTrackingCodeAndCourseCode(trackingCode,
            courseCode);

      Student student = getStudent(trackingCode);

      return new StudentCourseDto(student, selectedCourse);

   }

   public Student getStudent(String trackingCode) {
      return applicationRepo.findStudentByTrackingCode(trackingCode);

   }

}
