package com.efeakif.intibak_control.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ValidateResponseDto {

    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    private boolean valid = true;

    public void addError(String error) {
        errors.add(error);
        valid = false;
    }

    public void addWarning(String warning) {
        warnings.add(warning);
    }

}
