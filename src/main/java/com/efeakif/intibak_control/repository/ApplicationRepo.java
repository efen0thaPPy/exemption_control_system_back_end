package com.efeakif.intibak_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.SelectedCourse;
import com.efeakif.intibak_control.entity.Student;

import java.util.List;
import com.efeakif.intibak_control.enums.ApplicationStatus;

public interface ApplicationRepo extends JpaRepository<Application, Long> {

    List<Application> findByApplicationStatus(ApplicationStatus applicationStatus);

    Application findByTrackingCode(String trackingCode);

    @Query("select a.transcriptPdfPath from Application a where a.trackingCode=:trackingCode")
    String findTranscriptPdfPathByTrackingCode(@Param("trackingCode") String trackingCode);

    @Query("select sc.coursePdfPath from Application a join a.selectedCourses sc where a.trackingCode=:trackingCode and sc.courseCode=:courseCode")
    String findCourseContentPdfByTrackingCodeAndCourseCode(@Param("courseCode") String courseCode,
            @Param("trackingCode") String trackingCode);

    @Query("select sc from Application a join a.selectedCourses sc where a.trackingCode=:trackingCode and sc.courseCode=:courseCode")
    SelectedCourse findSelectedCourseByTrackingCodeAndCourseCode(@Param("trackingCode") String trackingCode,
            @Param("courseCode") String courseCode);

    @Query("select a.student from Application a where a.trackingCode=:trackingCode")
    Student findStudentByTrackingCode(@Param("trackingCode") String trackingCode);

}
