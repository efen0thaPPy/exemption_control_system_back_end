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
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "selected_course_id")
  private SelectedCourse selectedCourse;

  @Column(columnDefinition = "TEXT")
  private String llmReport;

  @Column(columnDefinition = "TEXT")
  private String missingTopics;

  @Column(nullable = false)
  private Double vectorSimilarityPercent;

  @Column(nullable = false)
  private Double llmSimilarityPercent;

  @Column(nullable = false)
  private String courseCode;

  @Column(nullable = false)
  private String courseName;

}
