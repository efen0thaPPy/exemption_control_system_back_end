package com.efeakif.intibak_control.dto.request;

import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SelectedCourseRequestDto {

    @NotEmpty
    private String courseCode;

    @NotEmpty
    private String courseName;

    @NotEmpty
    private String semester;

    private CourseLanguage courseLanguage;

    @NotEmpty
    private CourseType courseType;

    @NotEmpty
    private Double ects;

    @NotEmpty
    private Grade grade;

    @JsonIgnore
    private MultipartFile coursePdf;

    @NotEmpty
    private String targetCourseCode;

    @Enumerated(EnumType.STRING)
    private ExemptionStatus exemptionStatus;

}
