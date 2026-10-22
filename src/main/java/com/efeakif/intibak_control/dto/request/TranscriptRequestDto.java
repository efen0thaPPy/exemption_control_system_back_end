package com.efeakif.intibak_control.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;

@Data
public class TranscriptRequestDto {

    private String firstName;

    private String lastName;

    private String university;

    @JsonIgnore
    private MultipartFile transcriptFile;

}
