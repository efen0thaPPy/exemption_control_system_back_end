package com.efeakif.intibak_control.dto.llm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseContentDto {

    @NotNull(message = "course contents are required")
    private String courseContents;

}
