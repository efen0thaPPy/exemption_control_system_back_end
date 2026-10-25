package com.efeakif.intibak_control.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.efeakif.intibak_control.entity.ErciyesCourse;

public interface ErciyesCourseRepo extends JpaRepository<ErciyesCourse, Integer> {

    @Query("select c from ErciyesCourse c where c.academicYear=(Select max(c2.academicYear) from ErciyesCourse c2)")

    List<ErciyesCourse> findByMaxAcademicYear();

    @Query("select c from ErciyesCourse c where c.semesterNumber<=4 and c.academicYear=(Select max(c2.academicYear) from ErciyesCourse c2)")
    List<ErciyesCourse> findByMaxAcademicYearForVertical();

    @Query("select c from ErciyesCourse c where c.courseCode=:courseCode and c.academicYear=(Select max(c2.academicYear) from ErciyesCourse c2)")
    ErciyesCourse findByCourseCodeandMaxAcademicYear(@Param("courseCode") String courseCode);

    @Query("select max(c.academicYear) from ErciyesCourse c")
    String findMaxAcademicYear();

    List<ErciyesCourse> findByAcademicYear(String academicYear);

    @Query("Select c from ErciyesCourse c where c.academicYear=:academicYear and c.courseCode=:courseCode")
    ErciyesCourse findByAcademicYearAndCourseCode(@Param("academicYear") String academicYear,
            @Param("courseCode") String courseCode);

}
