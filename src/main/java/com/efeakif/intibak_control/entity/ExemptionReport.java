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
import jakarta.persistence.Lob;
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

    private String firstName;

    private String lastName;

    @Lob
    private String llmReport;

    @Lob
    private String missingTopics;

    @Column(nullable = false)
    private Double similarityPercent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExemptionStatus exemptionStatus;

    @OneToMany(mappedBy = "exemptionReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyExemptionReport> weeklyExemptionReports = new ArrayList<>();

}
