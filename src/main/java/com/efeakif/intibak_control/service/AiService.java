package com.efeakif.intibak_control.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.dto.llm.TranscriptDto;
import com.efeakif.intibak_control.enums.AiModel;
import com.efeakif.intibak_control.service.ai.LLMService;
import com.efeakif.intibak_control.service.document.PdfExtractionService;
import com.efeakif.intibak_control.service.document.TranscriptParserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

  private final PdfExtractionService pdfExtractionService;
  private final TranscriptParserService transcriptParserService;
  private final LLMService llmService;

  private static final String TRANSCRIPT_PROMPT = """
                              You are a precision extractor for Turkish YÖK / e-Devlet transcripts ("NOT DÖKÜM BELGESİ").
      The input is raw text extracted from the PDF. Extract the student info and every graded course block into JSON.

      ### HOW THE TEXT IS STRUCTURED
      1. SEMESTER HEADINGS: a line like "2023-2024 Güz Dönemi", "2023-2024 Bahar Dönemi" or "2023-2024 Yaz Okulu".
         Every course block after a heading belongs to that semester until the next heading.
      2. COURSE BLOCK: each course is a block of consecutive lines:
           103051_BZ                  <- code prefix (may start with "*")
           111                        <- course number
           BİLGİSAYAR PROGRAMLAMA I   <- course name (may continue on a 2nd line)
           (COMPUTER PROGRAMMING I)   <- English translation in parentheses: IGNORE
           Z Tr 4 0 5 5 DC 72 KL      <- row line
         The row line tokens are, in order:
           Status | Language | T | U | UK | AKTS | Grade | Points | Comment
           Z      | Tr       | 4 | 0 | 5  | 5    | DC    | 72     | KL
      3. NOISE: ignore lines containing DNO, GNO, TUK, TAKTS, DSD, Şeref, Başarılı, page numbers like "1 / 4", the barcode/verification footer, column headers (Dersin Statüsü, Öğretim Dili, AKTS, Not, Puan, Açıklama), and the student-info header that repeats on every page. Noise lines never start a course and never change the semester.
      4. PAGE BREAK: a block can be split by a page break, e.g.
           103051_BZ DIGITAL SYSTEMS LABORATORY Z İng. 0 2 2 2 BB 216 G
           ...page header lines...
           311 (DIGITAL SYSTEMS LABORATORY)
         -> courseCode "103051_BZ 311", courseName "DIGITAL SYSTEMS LABORATORY".

      ### FIELD RULES (every value of an item comes from ONE block only - never mix values from neighbouring blocks)
      - courseCode: prefix + " " + course number, leading "*" removed. Example: "*103051_65510" + "1" -> "103051_65510 1".
      - courseName: the name line(s) before the parenthesized line.
      - courseLanguage: Language token of the row line: "Tr" -> "TURKISH", "İng." -> "ENGLISH".
      - ects: the AKTS token (6th token) of the row line, as a number.
      - grade: the Grade token (7th token) of the row line.
      - semester: the closest semester heading ABOVE the block.

      ### WHICH BLOCKS TO OUTPUT
      - Output one item per block whose grade is a letter grade (AA, BA, BB, CB, CC, DC, DD, FD, FF, S, M, ET), in the order the blocks appear.
      - Skip blocks whose grade is "-". If every block of a semester has "-", output nothing from it and close the array.
      - Retakes: a course that appears again in a later semester (Comment contains "TKR") is a NEW block. Output it as its own item with its own semester and grade, even though its courseCode already appeared earlier. Never output the same block twice.

      ### EXAMPLE
      Input block (under heading "2023-2024 Güz Dönemi"):
        103051_10305
        10063
        PHYSICS I
        (PHYSICS I)
        Z İng. 3 1 6 6 BB 195 EŞD G
      Output item:
        {"courseCode": "103051_10305 10063", "courseName": "PHYSICS I", "semester": "2023-2024 Güz Dönemi", "courseLanguage": "ENGLISH", "ects": 6, "grade": "BB"}

      ### STUDENT INFO
      - The labels "Öğrenci No", "T.C. Kimlik No", "Adı", "Doğum Tarihi" are followed by their values in the same order. firstName = the value for "Adı".
      - lastName = the text after "Soyadı (Surname) :".
      - university = the line ending in "ÜNİVERSİTESİ".

      ### OUTPUT FORMAT
      Return strictly a valid JSON object matching this schema:
      {
        "firstName": "string",
        "lastName": "string",
        "university": "string",
        "transcriptCourses": [
          {
            "courseCode": "string",
            "courseName": "string",
            "semester": "string",
            "courseLanguage": "TURKISH" | "ENGLISH",
            "ects": number,
            "grade": "string"
          }
        ]
      }
                                 """;
  private static final String COURSE_CONTENT_MAPPER = """
              You are an academic course syllabus parser. Your task is to extract the pure academic subject topics from the provided syllabus document.

      EXTRACTION INSTRUCTIONS:



      1. COURSE CONTENTS (courseContents):
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

  @Transactional
  public CourseContentDto analyzeCourseContents(String contents) throws IOException {

    CourseContentDto courseContentDto = llmService.converse(AiModel.llama31, COURSE_CONTENT_MAPPER, contents,
        CourseContentDto.class);

    return courseContentDto;

  }

  @Transactional
  public CourseContentDto analyzeCourseContents(MultipartFile contents) throws IOException {

    String text = pdfExtractionService.extractRawTextFromMultipart(contents);

    CourseContentDto courseContentDto = llmService.converse(AiModel.llama31, COURSE_CONTENT_MAPPER, text,
        CourseContentDto.class);

    return courseContentDto;

  }

  @Transactional
  public TranscriptDto analyzeTranscript(MultipartFile transcript) throws IOException {

    String transcriptText = pdfExtractionService.extractRawTextFromMultipart(transcript);

    TranscriptDto transcriptDto = transcriptParserService.parseTranscript(transcriptText);

    if (transcriptDto == null || transcriptDto.getTranscriptCourses().isEmpty()) {
      System.out.println("parse failed, fallbacking to llm");

      transcriptDto = llmService.converse(AiModel.gemini36, TRANSCRIPT_PROMPT, transcriptText,
          TranscriptDto.class);
    }

    HashMap<String, TranscriptDto.TranscriptCourses> uniqueCourses = new LinkedHashMap<>();

    if (transcriptDto != null && transcriptDto.getTranscriptCourses() != null) {

      for (TranscriptDto.TranscriptCourses courses : transcriptDto.getTranscriptCourses()) {
        if (courses.getCourseCode() != null && !courses.getCourseCode().isBlank()
            && courses.getGrade() != null) {

          uniqueCourses.put(courses.getCourseCode(), courses);
        }

      }

      transcriptDto.setTranscriptCourses(new ArrayList<>(uniqueCourses.values()));
    }
    return transcriptDto;

  }

}
