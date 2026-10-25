package com.efeakif.intibak_control.entity;

import java.util.ArrayList;
import java.util.List;

import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.CourseType;
import com.efeakif.intibak_control.enums.Grade;
import com.efeakif.intibak_control.enums.TransferType;

import groovyjarjarantlr4.v4.runtime.misc.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "application_id")
    private long id;

    @Embedded
    private Student student;

    @Column(nullable = false, unique = true, updatable = false)
    private String trackingCode;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferType transferType;

    private String transcriptPdfPath;

    @Column(nullable = false)
    private String academicYear;

    // @ElementCollection
    // @CollectionTable(name = "application_courses", joinColumns = @JoinColumn(name
    // = "application_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
    // "application_id", "course_code", "target_course_code" }))

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SelectedCourse> selectedCourses;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LLMExemptionReport> llmExemptionReports = new ArrayList<>();

}
