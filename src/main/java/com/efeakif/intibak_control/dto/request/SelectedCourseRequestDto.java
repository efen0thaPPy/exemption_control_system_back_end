package com.efeakif.intibak_control.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectedCourseRequestDto {

    private String courseCode;

    private String courseName;

    private String semester;

    private CourseLanguage courseLanguage;

    private CourseType courseType;

    private Double ects;

    private Grade grade;

    @JsonIgnore
    private MultipartFile coursePdf;

    private String targetCourseCode;

    @Enumerated(EnumType.STRING)
    private ExemptionStatus exemptionStatus;

}
