package com.efeakif.intibak_control.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationUserResponseDto {

    public ApplicationUserResponseDto(String trackingCode, String message) {
        this.trackingCode = trackingCode;
        this.message = message;
    }

    private String trackingCode;
    private String message;

}
