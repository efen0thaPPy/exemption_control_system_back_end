package com.efeakif.intibak_control.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.enums.CourseLanguage;
import com.efeakif.intibak_control.enums.Grade;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class TranscriptRequestDto {

    @NotBlank(message = "first name is required")
    private String firstName;

    @NotBlank(message = "last name is required")
    private String lastName;

    @NotBlank(message = "university is required")
    private String university;

    @JsonIgnore
    private MultipartFile transcriptFile;

}
