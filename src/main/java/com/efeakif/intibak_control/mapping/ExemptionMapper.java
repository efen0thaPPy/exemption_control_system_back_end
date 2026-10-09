package com.efeakif.intibak_control.mapping;

import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.stereotype.Component;

import com.efeakif.intibak_control.dto.llm.CourseListDto;
import com.efeakif.intibak_control.dto.llm.ExemptionDto;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.entity.ExemptionReport;
import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

@Component
public class ExemptionMapper {

    public ExemptionReport toEntity(ExemptionDto exemptionDto, CourseListDto.CourseContentDto sourceCourse,
            Document doc, CourseListDto studentInfo) {

        ExemptionReport exemptionReport = new ExemptionReport();
        exemptionReport.setFirstName(studentInfo.getFirstName());

        if (exemptionDto.getSimilarityPercent() < 60)
            exemptionReport.setExemptionStatus(ExemptionStatus.FAILED);

        else if (exemptionDto.getSimilarityPercent() >= 60 && exemptionDto.getSimilarityPercent() < 80)
            exemptionReport.setExemptionStatus(ExemptionStatus.MANUAL_CHECK_REQUIRED);

        else
            exemptionReport.setExemptionStatus(ExemptionStatus.PASSED);

        exemptionReport.setLastName(studentInfo.getLastName());
        exemptionReport.setLlmReport(exemptionDto.getReport());
        exemptionReport.setSimilarityPercent(exemptionDto.getSimilarityPercent());
        exemptionReport.setSourceAkts(sourceCourse.getAkts());
        exemptionReport.setMissingTopics(exemptionDto.getMissingTopics());
        exemptionReport.setSourceCourseName(sourceCourse.getCourseName());
        exemptionReport.setUniversity(studentInfo.getUniversity());
        exemptionReport.setTargetCourseName((String) doc.getMetadata().get("courseName"));
        Number number = (Number) doc.getMetadata().get("akts");
        if (number != null)
            exemptionReport.setTargetAkts(number.doubleValue());
        exemptionReport.setAcademicYear((String) doc.getMetadata().get("academicYear"));
        exemptionReport.setSourceGrade(sourceCourse.getGrade());

        return exemptionReport;

    }

    public ExemptionResponseDto toDto(ExemptionReport exemptionReport) {

        ExemptionResponseDto exemptionResponseDto = new ExemptionResponseDto();
        exemptionResponseDto.setFirstName(exemptionReport.getFirstName());
        exemptionResponseDto.setLastName(exemptionReport.getLastName());
        exemptionResponseDto.setLlmReport(exemptionReport.getLlmReport());
        exemptionResponseDto.setSimilarityPercent(exemptionReport.getSimilarityPercent());
        exemptionResponseDto.setSourceAkts(exemptionReport.getSourceAkts());
        exemptionResponseDto.setMissingTopics(exemptionReport.getMissingTopics());
        exemptionResponseDto.setSourceCourseName(exemptionReport.getSourceCourseName());
        exemptionResponseDto.setUniversity(exemptionReport.getUniversity());
        exemptionResponseDto.setSourceGrade(exemptionReport.getSourceGrade());
        exemptionResponseDto.setExemptionStatus(exemptionReport.getExemptionStatus());
        exemptionResponseDto.setTargetCourseName(exemptionReport.getTargetCourseName());
        exemptionResponseDto.setTargetAkts(exemptionReport.getTargetAkts());
        exemptionResponseDto.setAcademicYear(exemptionReport.getAcademicYear());

        return exemptionResponseDto;

    }

}
