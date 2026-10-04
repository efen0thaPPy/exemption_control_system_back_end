package com.efeakif.intibak_control.entity;

import java.util.ArrayList;
import java.util.List;

import com.efeakif.intibak_control.enums.ExemptionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "exemption_reports")
public class ExemptionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exemption_report_id")
    private int id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String sourceCourseName;

    @Column(nullable = false)
    private String targetCourseName;

    @Column(nullable = false)
    private String sourceAkts;

    @Column(nullable = false)
    private String targetAkts;

     @Column(nullable = false)
     private String sourceGrade;

      @Column(nullable = false)
      private String targetGrade;

    

    @Column(columnDefinition = "TEXT")
    private String llmReport;

    @Column(columnDefinition = "TEXT")
    private String missingTopics;

    @Column(nullable = false)
    private Double similarityPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionStatus exemptionStatus;

    @OneToMany(mappedBy = "exemptionReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyExemptionReport> weeklyExemptionReports = new ArrayList<>();

}
