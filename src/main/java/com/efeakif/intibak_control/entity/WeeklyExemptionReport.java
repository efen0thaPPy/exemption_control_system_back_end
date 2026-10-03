package com.efeakif.intibak_control.entity;

import com.efeakif.intibak_control.enums.Coverage;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "weekly_exemption_reports")
public class WeeklyExemptionReport {

    @PrePersist
    @PreUpdate
    private void checkSourceContent() {
        if (coverage != Coverage.NOT_COVERED)
            if (matchedWeekContent == null || matchedWeekContent.trim().isEmpty())
                throw new IllegalStateException("matched content can't be empty while coverage is" + coverage);

    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "weekly_exemption_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Coverage coverage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sourceContent;

    @Column(nullable = false)
    private int sourceWeekNumber;

    @Column(columnDefinition = "TEXT")
    private String matchedWeekContent;

    @ManyToOne
    private ExemptionReport exemptionReport;

}
