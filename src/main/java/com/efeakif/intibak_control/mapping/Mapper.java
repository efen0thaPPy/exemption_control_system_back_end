package com.efeakif.intibak_control.mapping;

import org.springframework.ai.document.Document;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.stereotype.Component;

import com.efeakif.intibak_control.dto.llm.CourseContentDto;
import com.efeakif.intibak_control.dto.llm.ExemptionDto;
import com.efeakif.intibak_control.dto.request.SelectedCourseRequestDto;
import com.efeakif.intibak_control.dto.request.TranscriptRequestDto;
import com.efeakif.intibak_control.dto.response.ErciyesCourseResponseDto;
import com.efeakif.intibak_control.dto.response.ExemptionResponseDto;
import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.entity.LLMExemptionReport;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;

import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

@Component
public class Mapper {

    public LLMExemptionReport toEntity(ExemptionDto exemptionDto, SelectedCourse selectedCourse,
            Document doc) {
        LLMExemptionReport exemptionReport = new LLMExemptionReport();

        exemptionReport.setLlmReport(exemptionDto.getReport());
        exemptionReport.setLlmSimilarityPercent(exemptionDto.getSimilarityPercent());
        exemptionReport.setMissingTopics(exemptionDto.getMissingTopics());

        double vectorPercent = Math.round(doc.getScore() * 100);
        exemptionReport.setVectorSimilarityPercent(vectorPercent);
        exemptionReport.setSelectedCourse(selectedCourse);

        exemptionReport.setCourseCode((String) doc.getMetadata().get("courseCode"));
        exemptionReport.setCourseName((String) doc.getMetadata().get("courseName"));

        return exemptionReport;

    }

    public ExemptionResponseDto toDto(LLMExemptionReport exemptionReport) {

        ExemptionResponseDto exemptionResponseDto = new ExemptionResponseDto();

        exemptionResponseDto.setLlmReport(exemptionReport.getLlmReport());

        exemptionResponseDto.setLlmSimilarityPercent(exemptionReport.getLlmSimilarityPercent());

        exemptionResponseDto.setVectorSimilarityPercent(exemptionReport.getVectorSimilarityPercent());

        exemptionResponseDto.setMissingTopics(exemptionReport.getMissingTopics());

        exemptionResponseDto.setSourceCourseCode(exemptionReport.getSelectedCourse().getCourseCode());
        exemptionResponseDto.setSourceCourseName(exemptionReport.getSelectedCourse().getCourseName());
        exemptionResponseDto.setTargetCourseCode(exemptionReport.getCourseCode());
        exemptionResponseDto.setTargetCourseName(exemptionReport.getCourseName());

        return exemptionResponseDto;

    }

    public Student toEntity(TranscriptRequestDto transcriptRequestDto) {

        Student student = new Student();
        student.setFirstName(transcriptRequestDto.getFirstName());
        student.setLastName(transcriptRequestDto.getLastName());
        student.setUniversity(transcriptRequestDto.getUniversity());

        return student;

    }

    public ErciyesCourseResponseDto toDto(ErciyesCourse erciyes) {
        ErciyesCourseResponseDto erciyesCourseResponseDto = new ErciyesCourseResponseDto();
        erciyesCourseResponseDto.setCourseCode(erciyes.getCourseCode());
        erciyesCourseResponseDto.setCourseName(erciyes.getCourseName());
        erciyesCourseResponseDto.setCourseType(erciyes.getCourseType());
        erciyesCourseResponseDto.setEcts(erciyes.getEcts());
        erciyesCourseResponseDto.setSemesterName(erciyes.getSemesterName());

        return erciyesCourseResponseDto;

    }

}
