package com.efeakif.intibak_control.service.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.entity.ErciyesCourse;

import com.efeakif.intibak_control.enums.CourseLanguage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VectorStoreService {
    private final VectorStore vectorStore;

    public void saveVectors(List<ErciyesCourse> courses) {

        List<Document> documents = new ArrayList<>();

        for (ErciyesCourse course : courses) {
            List<String> topics = course.getWeeklyContents().stream().map(x -> x.getDescription()).toList();
            String textToEmbed = course.getCourseName() + "\n\n" + String.join(", ", topics);
            Map<String, Object> metadata = Map.of(
                    "courseId", course.getId(),
                    "courseName", course.getCourseName(),
                    "academicYear", course.getAcademicYear(),
                    "akts", course.getEcts());

            documents.add(new Document(textToEmbed, metadata));
        }
        vectorStore.add(documents);

    }

    public List<Document> findSimilarCourses(String name, String descriptions, String academicYear) {

        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                .query(name + "\n\n" + descriptions)
                .topK(2)
                .similarityThreshold(0.60);

        if (academicYear != null && !academicYear.isBlank()) {

            searchRequestBuilder.filterExpression(String.format("academicYear=='%s'", academicYear));
        }

        SearchRequest searchRequest = searchRequestBuilder.build();

        return vectorStore.similaritySearch(searchRequest);

    }

}
