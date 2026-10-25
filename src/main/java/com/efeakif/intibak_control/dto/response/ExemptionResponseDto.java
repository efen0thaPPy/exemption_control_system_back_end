package com.efeakif.intibak_control.dto.response;

import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ExemptionResponseDto {

    private String sourceCourseName;

    private String targetCourseName;

    private String academicYear;

    private String llmReport;

    private String missingTopics;

    private Double llmSimilarityPercent;

    private Double vectorSimilarityPercent;

}
