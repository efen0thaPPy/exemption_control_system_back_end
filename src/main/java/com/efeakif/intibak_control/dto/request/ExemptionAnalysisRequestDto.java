package com.efeakif.intibak_control.dto.request;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

@Data
public class ExemptionAnalysisRequestDto {

    @NotNull(message = "course contents are required")
    private CourseContentDto courseContentDto;

    @NotNull(message = "transcript is required")
    private TranscriptRequestDto transcriptRequestDto;

    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "academic year must match format YYYY-YYYY (e.g. 2023-2024)")
    private String academicYear;

}
