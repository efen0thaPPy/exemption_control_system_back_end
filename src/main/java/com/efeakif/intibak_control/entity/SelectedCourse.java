package com.efeakif.intibak_control.entity;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class SelectedCourse {

    
    private String courseCode;

    private String courseName;

    private String semester;

     @Enumerated (EnumType.STRING)
    private CourseLanguage courseLanguage;

     @Enumerated (EnumType.STRING)
    private CourseType courseType;

    private Double ects;

     @Enumerated (EnumType.STRING)
    private Grade grade;

    private String coursePdfPath;

    private String targetCourseCode;

    @Enumerated(EnumType.STRING)
    private ExemptionStatus exemptionStatus;

}
