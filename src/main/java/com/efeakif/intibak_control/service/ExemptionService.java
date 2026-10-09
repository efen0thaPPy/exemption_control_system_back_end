package com.efeakif.intibak_control.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.llm.CourseListDto;
import com.efeakif.intibak_control.dto.llm.ExemptionDto;

import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.entity.ExemptionReport;
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

        private static final String MAPPER_MERGER_PROMPT = """
                        You are an expert academic advisor and course equivalence evaluator.
                        Your role is to analyze student transcripts and syllabi, accurately matching courses
                        and extracting structured academic data without hallucinating information.
                        """;

        private static final String EXEMPTION_PROMPT = """
                        You are an expert academic advisor and course equivalence evaluator.
                        Your role is to analyze student's source syllabi and compare it to the target courses syllabi
                        and generating a similarity percent for the contents: You will also generate a report and missing topics to inform the exemption""";

        @Transactional
        public List<ExemptionResponseDto> analyzeExemption(MultipartFile transcript, MultipartFile courseContent)
                        throws IOException {

                List<ExemptionReport> reportsToSave = new ArrayList<>();

                String transcriptText = pdfExtractionService.extractRawTextFromPdf(transcript);
                String courseContentText = pdfExtractionService.extractRawTextFromPdf(courseContent);

                String mappingAndParsingPrompt = String.format(
                                "###TRANSCRIPT: \n %s \n ------------\n ###SYLLABI: \n %s \n",
                                transcriptText, courseContentText);

                CourseListDto courseListDto = llmService.converse(MAPPER_MERGER_PROMPT, mappingAndParsingPrompt,
                                CourseListDto.class);

                for (CourseListDto.CourseContentDto contentDto : courseListDto.getCourseContents()) {

                        if (!contentDto.getGrade().isPassing())
                                continue;

                        List<Document> matches = vectorStoreService.findSimilarCourses(contentDto);

                        if (matches.isEmpty())
                                continue;

                        String sourceText = String.format(
                                        "Source course name: %s, Source syllabi: \n%s\n---------------\n",
                                        contentDto.getCourseName(), contentDto.getDescription());

                        List<Document> ectsFiltered = matches.stream()
                                        .filter(e -> {

                                                Number num = (Number) e.getMetadata().get("akts");

                                                return num != null && (num.doubleValue() <= contentDto.getAkts());

                                        })
                                        .toList();

                        for (Document doc : ectsFiltered) {
                                String targetText = String.format(
                                                "Candidate course name: %s, SimilarityScore(%.2f),\n Candidate Syllabi:\n %s",
                                                doc.getMetadata().get("courseName"), doc.getScore(), doc.getText());

                                ExemptionDto exemptionDto = llmService.converse(EXEMPTION_PROMPT,
                                                sourceText + targetText,
                                                ExemptionDto.class);

                                ExemptionReport exemptionReport = mapper.toEntity(exemptionDto, contentDto, doc,
                                                courseListDto);

                                reportsToSave.add(exemptionReport);

                        }

                }
                exemptionRepo.saveAll(reportsToSave);

                return reportsToSave.stream().map(mapper::toDto).toList();

        }

}
