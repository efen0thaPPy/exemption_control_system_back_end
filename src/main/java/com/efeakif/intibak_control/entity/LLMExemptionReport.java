package com.efeakif.intibak_control.entity;

import java.util.ArrayList;
import java.util.List;

import com.efeakif.intibak_control.enums.CourseLanguage;

import com.efeakif.intibak_control.enums.Grade;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "exemption_reports")
public class LLMExemptionReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "exemption_report_id")
  private int id;

  @Column(nullable = false)
  private String sourceCourseName;

  @Column(nullable = false)
  private String targetCourseName;

  @Column(nullable = false)
  private String academicYear;

  @Column(columnDefinition = "TEXT")
  private String llmReport;

  @Column(columnDefinition = "TEXT")
  private String missingTopics;

  @Column(nullable = false)
  private Double vectorSimilarityPercent;

  private String sourceSemester;

  @Column(nullable = false)
  private Double llmSimilarityPercent;

  @ManyToOne
  @JoinColumn(name = "application_id")
  private Application application;

}
