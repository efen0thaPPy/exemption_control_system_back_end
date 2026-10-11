package com.efeakif.intibak_control.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;

import com.efeakif.intibak_control.dto.llm.ExemptionDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.dto.request.TranscriptRequestDto;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.entity.ExemptionReport;
import com.efeakif.intibak_control.enums.AiModel;
import com.efeakif.intibak_control.mapping.ExemptionMapper;
import com.efeakif.intibak_control.repository.ExemptionRepo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ExemptionService {

   private final LLMService llmService;
   private final PdfExtractionService pdfExtractionService;
   private final VectorStoreService vectorStoreService;
   private final ExemptionMapper mapper;
   private final ExemptionRepo exemptionRepo;

   private static final String TRANSCRIPT_PROMPT = """
                                                                        You are a precision academic transcript extractor for Turkish university transcripts (YÖK / e-Devlet format).
         Extract the student info and all completed/graded courses into valid JSON strictly matching the schema.

         ### HARD CONSTRAINTS:
         1. NO PARENTHESES IN courseCode:
            - Any text in parentheses like "(COMPUTER PROGRAMMING I)", "(CALCULUS I)", "(DISCRETE MATHEMATICS)" is a TRANSLATION SUBTITLE.
            - COMPLETELY DISCARD IT! NEVER put parentheses "(" or ")" inside courseCode!

         2. HOW courseCode IS FORMED:
            - Always combine the code prefix and the course number (Prefix + Space + Number):
              * "103051_BZ" and "111" -> "103051_BZ 111" (NOT "111 (COMPUTER PROGRAMMING I)")
              * "103051_BZ" and "101" -> "103051_BZ 101" (NOT "101 (CALCULUS I)")
              * "103051_BZ" and "108" -> "103051_BZ 108" (NOT "108 (CALCULUS II)")
              * "103051_BZ" and "106" -> "103051_BZ 106" (NOT "106 (DISCRETE MATHEMATICS)")
              * "103051_BZ" and "102" -> "103051_BZ 102"
              * "103051_BZ" and "203" -> "103051_BZ 203"
              * "103051_BZ" and "205" -> "103051_BZ 205"
              * "103051_10305" and "10065" -> "103051_10305 10065"
              * "103051_10305" and "10055" -> "103051_10305 10055"
              * "103051_10305" and "10064" -> "103051_10305 10064"
            - Remove any leading "*" asterisk.

         3. courseName:
            - The primary official course title (e.g. "BİLGİSAYAR PROGRAMLAMA I", "MATEMATİK I", "PHYSICS I").
            - NEVER empty.

         4. ects (AKTS):
            - ALWAYS the 4th credit number before the grade. MUST BE A VALID NUMBER (e.g. 6.0, 5.0, 2.0). NEVER a string or "-".

         5. grade & MANDATORY TERMINATION:
            - ONLY extract courses that have an actual letter grade: AA, BA, BB, CB, CC, DC, DD, FD, FF, S, M, ET.
            - If a course has grade "-" (like ongoing/future term courses): DO NOT EXTRACT IT! SKIP COMPLETELY!
            - NEVER output grade: "-" or ects: "-".

         ### OUTPUT FORMAT:
         Return strictly a valid JSON object matching this schema:
         {
           "firstName": "string",
           "lastName": "string",
           "university": "string",
           "transcriptCourses": [
             {
               "courseCode": "string",
               "courseName": "string",
               "courseLanguage": "TURKISH" | "ENGLISH",
               "ects": number,
               "grade": "string"
             }
           ]
         }





                                                                                                            """;

   private static final String COURSE_CONTENT_MAPPER = """
                 You are an academic course syllabus parser. Your task is to extract the course code, course name, and pure academic subject topics from the provided syllabus document.

         EXTRACTION INSTRUCTIONS:
         1. COURSE CODE (courseCode):
            - Extract the value next to 'Ders Kodu'.

         2. COURSE NAME (courseName):
            - Extract the full course title next to 'Ders Adı'.

         3. COURSE CONTENTS (courseContents):
            - Extract the actual academic topics and concepts taught in the course.
            - FORMAT: A clean list of topics (comma-separated or bullet points), with no week numbers.
            - STRIP ALL WEEK LABELS: Completely remove all week prefixes and numbers (e.g., remove "1. Hafta / Week", "Hafta 1", "Week 1", etc.).
            - IGNORE EMPTY WEEKS: Do not include empty week lines (e.g., "15. Hafta / Week").
            - IGNORE EXAMS: Completely omit exam entries (e.g., "Arasınav", "Midterm", "Yarıyıl Sonu Sınavı", "Final").
            - DEDUPLICATE: If a topic is listed across multiple weeks (e.g., "Bağıntılar"), list it once.

         4. WHAT TO IGNORE:
            - Do NOT include evaluation percentages ('Değerlendirme Sistemi').
            - Do NOT include instructor names, workload calculations ('AKTS İş Yükü'), or program outcome matrices ('Program Çıktıları').

         5. OUTPUT:
            - Output only valid JSON adhering strictly to the CourseContentDto schema.


               """;
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
   public TranscriptDto analyzeTranscript(MultipartFile transcript) throws IOException {

      String transcriptText = pdfExtractionService.extractRawTextFromPdf(transcript);

      TranscriptDto transcriptDto = llmService.converse(AiModel.llama31, TRANSCRIPT_PROMPT, transcriptText,
            TranscriptDto.class);

      HashMap<String, TranscriptDto.TranscriptCourses> courses = new LinkedHashMap<>();

      if (transcriptDto != null && transcriptDto.getTranscriptCourses() != null) {

         for (TranscriptDto.TranscriptCourses transcriptCourses : transcriptDto.getTranscriptCourses()) {
            if (transcriptCourses.getCourseCode() != null && !transcriptCourses.getCourseCode().isBlank()) {

               courses.put(transcriptCourses.getCourseCode(), transcriptCourses);
            }

         }

         transcriptDto.setTranscriptCourses(new ArrayList<>(courses.values()));
      }
      return transcriptDto;

   }

   @Transactional
   public CourseContentDto analyzeCourseContents(MultipartFile contents) throws IOException {

      String contentText = pdfExtractionService.extractRawTextFromPdf(contents);
      CourseContentDto courseContentDto = llmService.converse(AiModel.llama31, COURSE_CONTENT_MAPPER, contentText,
            CourseContentDto.class);

      return courseContentDto;

   }

   @Transactional
   public List<ExemptionResponseDto> exemptCourse(TranscriptRequestDto transcriptRequestDto,
         CourseContentDto courseContentDto) throws IOException {

      List<Document> documents = vectorStoreService.findSimilarCourses(courseContentDto.getCourseName(),
            courseContentDto.getCourseContents());

      List<ExemptionResponseDto> exemptionResponseDtos = new ArrayList<>();

      for (Document doc : documents) {
         if (doc == null)
            continue;

         

         System.out.println(
               String.format("CourseName: %s, Score: %s", doc.getMetadata().get("courseName"), doc.getScore()));

         String sourceText = String.format(
               "Source course name: %s,\n Source Syllabi: \n%s\n---------------\n",
               courseContentDto.getCourseName(), courseContentDto.getCourseContents());

         String targetText = String.format(
               "Candidate course name: %s,\n Candidate Syllabi:\n %s",
               doc.getMetadata().get("courseName"), doc.getText());

         ExemptionDto exemptionDto = llmService.converse(AiModel.llama31, EXEMPTION_PROMPT, sourceText + targetText,
               ExemptionDto.class);

         ExemptionReport exemptionReport = mapper.toEntity(exemptionDto, transcriptRequestDto, doc);

         exemptionResponseDtos.add(mapper.toDto(exemptionReport));
      }

      return exemptionResponseDtos;

   }

}
