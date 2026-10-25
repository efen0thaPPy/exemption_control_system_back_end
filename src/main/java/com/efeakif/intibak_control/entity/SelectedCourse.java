package com.efeakif.intibak_control.entity;

import java.util.List;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SelectedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "selected_course_id")
    private long id;

    private String courseCode;

    private String courseName;

    private String semester;

    @Enumerated(EnumType.STRING)
    private CourseLanguage courseLanguage;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private Double ects;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    private String coursePdfPath;

    @Embedded
    private CourseExemptionReport courseExemptionReport;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

}
