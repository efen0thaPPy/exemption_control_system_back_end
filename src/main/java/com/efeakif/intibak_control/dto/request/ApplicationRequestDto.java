package com.efeakif.intibak_control.dto.request;

import java.util.List;

import com.efeakif.intibak_control.enums.TransferType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDto {
    @Valid
    @NotNull(message = "transcript request can't be null")
    private TranscriptRequestDto transcriptRequestDto;

    @Valid
    @NotNull(message = "courses can't be null")
    private List<SelectedCourseRequestDto> courses;

    @NotNull(message = "transferType can't be null")
    private TransferType transferType;

    @NotNull(message = "academic year can't ne null")
    private String academicYear;

}
