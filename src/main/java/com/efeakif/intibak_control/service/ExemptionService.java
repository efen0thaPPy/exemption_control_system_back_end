package com.efeakif.intibak_control.service;

import com.efeakif.intibak_control.service.document.PdfExtractionService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.efeakif.intibak_control.dto.internal.StudentCourseDto;
import com.efeakif.intibak_control.dto.llm.CourseContentDto;

import com.efeakif.intibak_control.dto.llm.ExemptionDto;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.LLMExemptionReport;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;
import com.efeakif.intibak_control.enums.AiModel;
import com.efeakif.intibak_control.exception.StaleAcademicYearException;
import com.efeakif.intibak_control.mapping.Mapper;
import com.efeakif.intibak_control.repository.ExemptionRepo;
import com.efeakif.intibak_control.service.ai.LLMService;
import com.efeakif.intibak_control.service.ai.VectorStoreService;
import com.efeakif.intibak_control.service.document.FileStorageService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExemptionService {

   private final PdfExtractionService pdfExtractionService;
   private final LLMService llmService;
   private final ApplicationService applicationService;
   private final AiService aiService;
   private final ExemptionRepo exemptionRepo;

   private final VectorStoreService vectorStoreService;
   private final FileStorageService fileStorageService;
   private final ErciyesCourseService erciyesCourseService;
   private final Mapper mapper;

   private static final String EXEMPTION_PROMPT = """
         You are a university academic equivalence evaluator.
         A student has taken a course ('Source Course') and is applying for exemption from a university course ('Target Course').

         CORE RULES:
         1. DIRECTION OF EVALUATION:
            - You must verify if the TARGET COURSE's weekly topics were covered by the STUDENT's Source Course.
            - If the student covered extra topics that the Target Course does not have, this is a GOOD thing—do NOT penalize the student and do NOT mark them as missing!

         2. MISSING TOPICS:
            - 'missingTopics' must ONLY list topics required by the TARGET COURSE that the STUDENT FAILED to cover.
            - If the student covered all of the Target Course's syllabus, write "None".

         3. SIMILARITY SCORE:
            - (Count of Target Course topics covered by Student / Total Target Course topics) * 100.
            - If the courses are identical in subject (e.g. Linear Algebra vs Lineer Cebir) and cover core matrices, vectors, and linear equations, the score should be 90-100%.

         OUTPUT FORMAT (Valid JSON only):
         {
           "missingTopics": "Target topics not covered by student, or 'None'",
           "report": "Clear evaluation explaining the overlap",
           "similarityPercent": <number between 0.0 and 100.0>
         }
         """;

   @Transactional
   public List<ExemptionResponseDto> exemptCourse(String trackingCode, long courseId)
         throws IOException {

      Application application = applicationService.findByTrackingCode(trackingCode);

      List<ExemptionResponseDto> responseDtos = new ArrayList<>();

      StudentCourseDto studentCourseDto = applicationService.getStudentCourse(trackingCode, courseId);

      Student student = studentCourseDto.student();
      SelectedCourse selectedCourse = studentCourseDto.selectedCourse();

      Resource pdf = fileStorageService.loadFile(selectedCourse.getCoursePdfPath());

      String text = pdfExtractionService.extractRawTextFromResource(pdf);

      CourseContentDto contentDto = aiService.analyzeCourseContents(text);

      System.out.println(contentDto);

      String courseName = selectedCourse.getCourseName();
      String courseContent = contentDto.getCourseContents();

      List<Document> documents = vectorStoreService.findSimilarCourses(courseName,
            courseContent, application.getAcademicYear());

      for (Document doc : documents) {

         String sourceText = String.format(
               "Source course name: %s,\n Source Syllabi: \n%s\n---------------\n",
               courseName, courseContent);

         String targetText = String.format(
               "Candidate course name: %s,\n Candidate Syllabi:\n %s",
               doc.getMetadata().get("courseName"), doc.getText());

         ExemptionDto exemptionDto = llmService.converse(AiModel.llama31, EXEMPTION_PROMPT, sourceText + targetText,
               ExemptionDto.class);

         LLMExemptionReport exemptionReport = mapper.toEntity(exemptionDto, selectedCourse, doc);

         exemptionRepo.save(exemptionReport);

         ExemptionResponseDto exemptionResponseDto = mapper.toDto(exemptionReport);
         responseDtos.add(exemptionResponseDto);

      }
      return responseDtos;

   }

}
