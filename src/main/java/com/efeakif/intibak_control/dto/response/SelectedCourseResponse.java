package com.efeakif.intibak_control.dto.response;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class SelectedCourseResponse {
    private String courseCode;

    private String courseName;

    private String semester;

    private CourseLanguage courseLanguage;

    private CourseType courseType;

    private Double ects;

    private Grade grade;

    private String targetCourseCode;

    @Enumerated(EnumType.STRING)
    private ExemptionStatus exemptionStatus;

}
