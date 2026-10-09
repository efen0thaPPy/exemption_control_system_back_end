package com.efeakif.intibak_control.dto.llm;

import lombok.Data;

@Data
public class ExemptionDto {

    private double similarityPercent;

    private String report;

    private String missingTopics;

}
