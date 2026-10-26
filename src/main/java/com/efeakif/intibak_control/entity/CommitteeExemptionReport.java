package com.efeakif.intibak_control.entity;

import com.efeakif.intibak_control.enums.ExemptionStatus;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class CommitteeExemptionReport {

    @Enumerated(EnumType.STRING)
    private ExemptionStatus exemptionStatus;

    private String targetCourseCode;

}
