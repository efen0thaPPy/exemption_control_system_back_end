package com.efeakif.intibak_control.dto.request;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;

import lombok.Data;

@Data
public class ExemptionAnalysisRequestDto {

    private CourseContentDto courseContentDto;

    private TranscriptRequestDto transcriptRequestDto;

}
