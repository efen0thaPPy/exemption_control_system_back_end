package com.efeakif.intibak_control.dto.request;

import java.util.List;

import com.efeakif.intibak_control.enums.TransferType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequestDto {
    private TranscriptRequestDto transcriptRequestDto;
    private List<SelectedCourseRequestDto> courses;
    private TransferType transferType;

}
