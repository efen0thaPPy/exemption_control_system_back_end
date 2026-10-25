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
import com.efeakif.intibak_control.dto.response.SelectedCourseResponse;
import com.efeakif.intibak_control.dto.response.SimpleApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.ValidateResponseDto;
import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;
import com.efeakif.intibak_control.enums.AiModel;
import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.TransferType;
import com.efeakif.intibak_control.exception.DuplicateStatusException;
import com.efeakif.intibak_control.exception.ResourceNotFoundException;
import com.efeakif.intibak_control.exception.UnfilledStatusException;
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
   private final ErciyesCourseService erciyesCourseService;
   private final Mapper mapper;
   private final FileStorageService fileStorageService;
   private final ApplicationRepo applicationRepo;
   private final ErciyesCourseRepo erciyesRepo;
   private final ApplicationMapper applicationMapper;

   @Transactional
   public ValidateResponseDto validateApplication(ApplicationRequestDto applicationRequestDto) {

      ValidateResponseDto validateResponseDto = new ValidateResponseDto();

      if (!erciyesCourseService.findMaxAcademicYear().equals(applicationRequestDto.getAcademicYear())) {
         validateResponseDto.addError("Courses are updated, refresh your page to fetch the up-to-date ones");
      }

      int year = LocalDate.now().getYear();

      TransferType transferType = applicationRequestDto.getTransferType();
      List<SelectedCourseRequestDto> courses = applicationRequestDto.getCourses();

      for (SelectedCourseRequestDto course : courses) {

         if (!course.getGrade().isPassing()) {
            validateResponseDto.addError("you cant exempt a course that you failed" + course.getCourseName());
         }
         if (course.getCoursePdf() == null || course.getCoursePdf().isEmpty()) {
            validateResponseDto
                  .addError(String.format("Course code: %s doesnt have a coursefile", course.getCourseCode()));
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
         } else {

            if (course.getEcts() < erciyesCourse.getEcts()
                  && CourseType.COMMON_MANDATORY != course.getCourseType()) {
               validateResponseDto.addWarning(
                     String.format("The ects your course has is lower than of the target course %s",
                           erciyesCourse.getCourseName()));
            }

            if (transferType == TransferType.VERTICAL_TRANSFER && erciyesCourse.getSemesterNumber() > 4) {

               validateResponseDto.addError(
                     String.format(
                           "The course you want your course exempted with (%s) is not in the first 4 semester (%s. semester) ",
                           erciyesCourse.getCourseName(), erciyesCourse.getSemesterNumber()));

            }
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
      String academicYear = applicationRequestDto.getAcademicYear();

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
      application.setAcademicYear(academicYear);

      List<SelectedCourse> selectedCourses = new ArrayList<>();

      for (SelectedCourseRequestDto selectedCourseRequestDto : courses) {

         String suffix = selectedCourseRequestDto.getCourseName().replaceAll("[^a-zA-Z0-9.-]", "_");

         String coursePdfPath = fileStorageService.saveFile(selectedCourseRequestDto.getCoursePdf(), trackingCode,
               suffix);

         SelectedCourse selectedCourse = applicationMapper.toSelectedCourseEntity(selectedCourseRequestDto,
               coursePdfPath);
         selectedCourse.setApplication(application);
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

      return applicationMapper.toApplicationResponseDto(application);

   }

   public Application getApplicationEntity(String trackingCode) {

      Application application = applicationRepo.findByTrackingCode(trackingCode);

      return application;

   }

   public Resource getTranscript(String trackingCode) {

      String path = applicationRepo.findTranscriptPdfPathByTrackingCode(trackingCode);

      if (path == null || path.isBlank())
         throw new ResourceNotFoundException("url doesnt exist");

      return fileStorageService.loadFile(path);

   }

   public Resource findCourseContentUrl(long courseId, String trackingCode) {

      String path = applicationRepo.findCourseContentPdfByTrackingCodeAndCourseId(courseId, trackingCode);

      if (path == null || path.isBlank())
         throw new ResourceNotFoundException("url doesnt exist");

      return fileStorageService.loadFile(path);

   }

   public StudentCourseDto getStudentCourse(String trackingCode, long courseId) {

      SelectedCourse selectedCourse = getCourseEntity(trackingCode, courseId);

      Student student = getStudent(trackingCode);

      return new StudentCourseDto(student, selectedCourse);

   }

   public Student getStudent(String trackingCode) {
      return applicationRepo.findStudentByTrackingCode(trackingCode);

   }

   public SelectedCourse getCourseEntity(String trackingCode, long courseId) {
      SelectedCourse selectedCourse = applicationRepo.findSelectedCourseByTrackingCodeAndCourseId(trackingCode,
            courseId);

      return selectedCourse;
   }

   @Transactional
   public SelectedCourseResponse manageCourse(String newTargetCourseCode,
         ExemptionStatus newExemptionStatus,
         String trackingCode,
         long courseId) {

      SelectedCourse selectedCourse = getCourseEntity(trackingCode, courseId);

      if (selectedCourse == null) {
         throw new ResourceNotFoundException(String.format(
               "there is no course with the id %s in the application with the tracking code of %s", courseId,
               trackingCode));
      }

      if (newTargetCourseCode != null && !newTargetCourseCode.isBlank()) {

         String academicYear = findAcademicYearByTrackingCode(trackingCode);

         ErciyesCourse erciyesCourse = erciyesRepo.findByAcademicYearAndCourseCode(academicYear, newTargetCourseCode);
         if (erciyesCourse == null)
            throw new ResourceNotFoundException("the target course couldn't be found");

         selectedCourse.getCourseExemptionReport().setTargetCourseCode(newTargetCourseCode);
      }

      if (newExemptionStatus != null) {
         if (selectedCourse.getCourseExemptionReport().getExemptionStatus() == newExemptionStatus)
            throw new DuplicateStatusException("the exemptionstatus is already the same as in the database");

         selectedCourse.getCourseExemptionReport().setExemptionStatus(newExemptionStatus);

      }

      return applicationMapper.toSelectedCourseResponseDto(selectedCourse);

   }

   @Transactional
   public SelectedCourseResponse addCourse(String trackingCode,
         SelectedCourseRequestDto selectedCourseRequestDto,
         MultipartFile coursePdfFile) {

      String pdfPath = fileStorageService.saveFile(coursePdfFile, trackingCode,
            selectedCourseRequestDto.getCourseCode());

      SelectedCourse selectedCourse = applicationMapper.toSelectedCourseEntity(selectedCourseRequestDto, pdfPath);

      Application application = applicationRepo.findByTrackingCode(trackingCode);

      application.getSelectedCourses().add(selectedCourse);
      selectedCourse.setApplication(application);

      return applicationMapper.toSelectedCourseResponseDto(selectedCourse);

   }

   @Transactional
   public ApplicationResponseDto manageApplicationStatus(ApplicationStatus applicationStatus, String trackingCode) {

      Application application = getApplicationEntity(trackingCode);

      if (application.getApplicationStatus() == applicationStatus)
         throw new DuplicateStatusException("application status is already same as in the database");

      List<SelectedCourse> selectedCourses = application.getSelectedCourses().stream()
            .filter(x -> x.getCourseExemptionReport().getExemptionStatus() == ExemptionStatus.PENDING).toList();

      if (!selectedCourses.isEmpty())
         throw new UnfilledStatusException("there are still pending status on the courses");

      application.setApplicationStatus(applicationStatus);

      return applicationMapper.toApplicationResponseDto(application);
   }

   @Transactional
   public String findAcademicYearByTrackingCode(String trackingCode) {
      return applicationRepo.findAcademicYearByTrackingCode(trackingCode);

   }

   @Transactional
   public Application findByTrackingCode(String trackingCode) {
      return applicationRepo.findByTrackingCode(trackingCode);

   }

   @Transactional
   public void deleteCourse(String trackingCode, long courseId) {

      Application application = findByTrackingCode(trackingCode);

      SelectedCourse selectedCourse = application.getSelectedCourses().stream().filter(x -> x.getId() == courseId)
            .findFirst().orElseThrow(() -> new ResourceNotFoundException("course not found with the id " + courseId));

      application.getSelectedCourses().remove(selectedCourse);
   }

}
