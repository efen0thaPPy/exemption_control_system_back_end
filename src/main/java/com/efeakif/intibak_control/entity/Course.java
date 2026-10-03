package com.efeakif.intibak_control.entity;

import java.util.ArrayList;
import java.util.List;

import com.efeakif.intibak_control.enums.CourseType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "courses", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_year", columnNames = { "course_code", "academic_year" }) })

public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "course_id")
    private int id;

    @Column(nullable = false, length = 10)
    private String courseCode;

    @Column(nullable = false, length = 100)
    private String courseName;

    private int practiceHours;

    @Column(nullable = false, length = 9)
    private String academicYear;

    private int theoryHours;

    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    private double localCredit;

    private double akts;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyContent> weeklyContents = new ArrayList<>();

}
