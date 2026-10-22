package com.efeakif.intibak_control.dto.response;

import java.util.List;

import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.TransferType;

import lombok.Data;

@Data
public class SimpleApplicationResponseDto {

    private String firstName;

    private String lastName;

    private String university;

    private String trackingCode;

    private ApplicationStatus applicationStatus;

    private TransferType transferType;

}
