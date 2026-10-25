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

    public LLMExemptionReport toEntity(ExemptionDto exemptionDto, Student student, SelectedCourse selectedCourse,
            Document doc, Application application) {
        LLMExemptionReport exemptionReport = new LLMExemptionReport();

        exemptionReport.setLlmReport(exemptionDto.getReport());
        exemptionReport.setLlmSimilarityPercent(exemptionDto.getSimilarityPercent());

        double vectorPercent = Math.round(doc.getScore() * 100);
        exemptionReport.setVectorSimilarityPercent(vectorPercent);
        exemptionReport.setSourceSemester(selectedCourse.getSemester());

        exemptionReport.setMissingTopics(exemptionDto.getMissingTopics());
        exemptionReport.setApplication(application);
        exemptionReport.setSourceCourseName(selectedCourse.getCourseName());

        exemptionReport.setTargetCourseName((String) doc.getMetadata().get("courseName"));

        exemptionReport.setAcademicYear((String) doc.getMetadata().get("academicYear"));

        return exemptionReport;

    }

    public ExemptionResponseDto toDto(LLMExemptionReport exemptionReport) {

        ExemptionResponseDto exemptionResponseDto = new ExemptionResponseDto();

        exemptionResponseDto.setLlmReport(exemptionReport.getLlmReport());

        exemptionResponseDto.setLlmSimilarityPercent(exemptionReport.getLlmSimilarityPercent());

        exemptionResponseDto.setVectorSimilarityPercent(exemptionReport.getVectorSimilarityPercent());

        exemptionResponseDto.setMissingTopics(exemptionReport.getMissingTopics());
        exemptionResponseDto.setSourceCourseName(exemptionReport.getSourceCourseName());

        exemptionResponseDto.setTargetCourseName(exemptionReport.getTargetCourseName());
        exemptionResponseDto.setAcademicYear(exemptionReport.getAcademicYear());

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
