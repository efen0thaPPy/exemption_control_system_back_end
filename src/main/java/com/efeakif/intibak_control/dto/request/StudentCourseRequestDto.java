package com.efeakif.intibak_control.dto.request;

import lombok.Data;

@Data
public class StudentCourseRequestDto {
    private String firstName;
    private String lastName;
    private String university;
    private String courseName;
    private String courseCode;
    private String courseEcts;
    private String courseLanguage;
    private String courseGrade;

}
