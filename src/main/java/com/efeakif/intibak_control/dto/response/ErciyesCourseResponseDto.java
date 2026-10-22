package com.efeakif.intibak_control.dto.response;

import com.efeakif.intibak_control.enums.CourseType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ErciyesCourseResponseDto {

    private String courseCode;

    private String courseName;

    private int practiceHours;

    private int theoryHours;

    private int semester;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private double ects;

}
