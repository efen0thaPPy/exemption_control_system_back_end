package com.efeakif.intibak_control.dto.response;

import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ExemptionResponseDto {

    private String firstName;

    private String lastName;

    private String university;

    private String sourceCourseName;

    private String targetCourseName;

    private String academicYear;

    private double sourceAkts;

    private double targetAkts;

    private Grade sourceGrade;

    private String llmReport;

    private String missingTopics;

    private Double similarityPercent;

    @Enumerated(EnumType.STRING)

    private ExemptionStatus exemptionStatus;

}
