package com.efeakif.intibak_control.dto.llm;

import java.util.List;

import com.efeakif.intibak_control.enums.Grade;

import lombok.Data;

@Data
public class CourseListDto {

    private String firstName;

    private String lastName;

    private String university;

    private List<CourseContentDto> courseContents;

    @Data
    public static class CourseContentDto {

        private String courseCode;

        private String courseName;

        private double akts;

        private Grade grade;

        private String description;

    }

}