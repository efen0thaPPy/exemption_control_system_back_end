package com.efeakif.intibak_control.entity;

import jakarta.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CourseContent {
    private String courseCode;

    private String courseName;

    private String courseContents;

}
