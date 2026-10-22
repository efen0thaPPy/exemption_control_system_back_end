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
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.entity.ExemptionReport;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;

import com.efeakif.intibak_control.enums.ExemptionStatus;
import com.efeakif.intibak_control.enums.Grade;

@Component
public class Mapper {

    public ExemptionReport toEntity(ExemptionDto exemptionDto, Student student, SelectedCourse selectedCourse,
            Document doc) {
        ExemptionReport exemptionReport = new ExemptionReport();
        exemptionReport.setFirstName(student.getFirstName());

        exemptionReport.setLastName(student.getLastName());
        exemptionReport.setLlmReport(exemptionDto.getReport());
        exemptionReport.setLlmSimilarityPercent(exemptionDto.getSimilarityPercent());

        double vectorPercent = Math.round(doc.getScore() * 100);
        exemptionReport.setVectorSimilarityPercent(vectorPercent);
        exemptionReport.setSourceSemester(selectedCourse.getSemester());
        exemptionReport.setSourceAkts(selectedCourse.getEcts());
        exemptionReport.setMissingTopics(exemptionDto.getMissingTopics());
        exemptionReport.setSourceCourseName(selectedCourse.getCourseName());
        exemptionReport.setUniversity(student.getUniversity());
        exemptionReport.setTargetCourseName((String) doc.getMetadata().get("courseName"));
        Number number = (Number) doc.getMetadata().get("akts");
        if (number != null)
            exemptionReport.setTargetAkts(number.doubleValue());
        exemptionReport.setAcademicYear((String) doc.getMetadata().get("academicYear"));
        exemptionReport.setSourceGrade(selectedCourse.getGrade());

        return exemptionReport;

    }

    public ExemptionResponseDto toDto(ExemptionReport exemptionReport) {

        ExemptionResponseDto exemptionResponseDto = new ExemptionResponseDto();
        exemptionResponseDto.setFirstName(exemptionReport.getFirstName());
        exemptionResponseDto.setLastName(exemptionReport.getLastName());
        exemptionResponseDto.setLlmReport(exemptionReport.getLlmReport());

        exemptionResponseDto.setLlmSimilarityPercent(exemptionReport.getLlmSimilarityPercent());

        exemptionResponseDto.setVectorSimilarityPercent(exemptionReport.getVectorSimilarityPercent());

        exemptionResponseDto.setSourceAkts(exemptionReport.getSourceAkts());
        exemptionResponseDto.setMissingTopics(exemptionReport.getMissingTopics());
        exemptionResponseDto.setSourceCourseName(exemptionReport.getSourceCourseName());
        exemptionResponseDto.setUniversity(exemptionReport.getUniversity());
        exemptionResponseDto.setSourceSemester(exemptionReport.getSourceSemester());
        exemptionResponseDto.setSourceGrade(exemptionReport.getSourceGrade());

        exemptionResponseDto.setTargetCourseName(exemptionReport.getTargetCourseName());
        exemptionResponseDto.setTargetAkts(exemptionReport.getTargetAkts());
        exemptionResponseDto.setAcademicYear(exemptionReport.getAcademicYear());

        return exemptionResponseDto;

    }

    public SelectedCourse toEntity(SelectedCourseRequestDto selectedCourseRequestDto, String path) {
        SelectedCourse selectedCourse = new SelectedCourse();
        selectedCourse.setCourseCode(selectedCourseRequestDto.getCourseCode());
        selectedCourse.setCourseLanguage(selectedCourseRequestDto.getCourseLanguage());
        selectedCourse.setCourseName(selectedCourseRequestDto.getCourseName());
        selectedCourse.setCourseType(selectedCourseRequestDto.getCourseType());
        selectedCourse.setSemester(selectedCourseRequestDto.getSemester());
        selectedCourse.setGrade(selectedCourseRequestDto.getGrade());
        selectedCourse.setEcts(selectedCourseRequestDto.getEcts());
        selectedCourse.setExemptionStatus(ExemptionStatus.PENDING);
        selectedCourse.setCoursePdfPath(path);
        selectedCourse.setTargetCourseCode(selectedCourseRequestDto.getTargetCourseCode());

        return selectedCourse;
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
        erciyesCourseResponseDto.setPracticeHours(erciyes.getPracticeHours());
        erciyesCourseResponseDto.setSemester(erciyes.getSemester());
        erciyesCourseResponseDto.setTheoryHours(erciyes.getTheoryHours());

        return erciyesCourseResponseDto;

    }

}
