package com.efeakif.intibak_control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.entity.Course;
import com.efeakif.intibak_control.entity.WeeklyContent;
import com.efeakif.intibak_control.enums.CourseLanguage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VectorStoreService {
    private final VectorStore vectorStore;

    public void saveVectors(List<Course> courses) {

        List<Document> documents = new ArrayList<>();

        for (Course course : courses) {
            List<String> topics = course.getWeeklyContents().stream().map(x -> x.getDescription()).toList();
            String textToEmbed = String.format("CourseName: %s, Descriptions: %s", course.getCourseName(), topics);
            Map<String, Object> metadata = Map.of(
                    "courseId", course.getId(),
                    "courseName", course.getCourseName(),
                    "academicYear", course.getAcademicYear(),
                    "akts", course.getEcts());

            documents.add(new Document(textToEmbed, metadata));
        }
        vectorStore.add(documents);

    }

    public List<Document> findSimilarCourses(String name, String descriptions) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(String.format("CourseName: %s, Descriptions: %s",
                        name,
                        descriptions))
                .topK(5)
                .similarityThreshold(0.70).build();

        return vectorStore.similaritySearch(searchRequest);

    }

}
