package com.efeakif.intibak_control.entity;

import com.efeakif.intibak_control.enums.ExemptionStatus;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CourseExemptionReport {

    private ExemptionStatus exemptionStatus;

    private String targetCourseCode;

    private String academicYear;

}
