package com.efeakif.intibak_control.dto.llm;

import java.util.List;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;

@Data
public class TranscriptDto {

    @JsonPropertyDescription("Ad/Given name")
    private String firstName;

    @JsonPropertyDescription("Soyad/Surname")
    private String lastName;

    @JsonPropertyDescription("ÜNİVERSİTE")
    private String university;

    private List<TranscriptCourses> transcriptCourses;

    @Data
    public static class TranscriptCourses {
        private String courseCode;
        private String courseName;

        private CourseLanguage courseLanguage;

        @JsonPropertyDescription("AKTS/ECTS")
        private Double ects;

        private Grade grade;
    }

}
