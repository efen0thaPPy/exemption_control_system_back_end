package com.efeakif.intibak_control.dto.response;

import java.util.List;

import com.efeakif.intibak_control.entity.Student;
import com.efeakif.intibak_control.enums.ApplicationStatus;
import com.efeakif.intibak_control.enums.TransferType;

import lombok.Data;

@Data
public class ApplicationResponseDto {

    private Student student;

    private String trackingCode;

    private ApplicationStatus applicationStatus;

    private TransferType transferType;

    private List<SelectedCourseResponse> selectedCourses;

}
