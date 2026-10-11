package com.efeakif.intibak_control.dto.llm;

import java.util.List;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TranscriptDto {

    @JsonPropertyDescription("Ad/Given name")
    @NotNull(message = "first name is required")
    private String firstName;

    @NotNull(message = "last name is required")
    @JsonPropertyDescription("Soyad/Surname")
    private String lastName;

    @NotNull(message = "university is required")
    @JsonPropertyDescription("ÜNİVERSİTE")
    private String university;

    @NotNull(message = "transcript courses are required")
    private List<TranscriptCourses> transcriptCourses;

    @Data
    public static class TranscriptCourses {

        @NotNull(message = "course code is required")
        private String courseCode;

        @NotNull(message = "course name is required")
        private String courseName;

        private String semester;

        private CourseLanguage courseLanguage;

        @JsonPropertyDescription("AKTS/ECTS")
        @NotNull(message = "ects is required")
        private Double ects;

        private Grade grade;
    }

}
