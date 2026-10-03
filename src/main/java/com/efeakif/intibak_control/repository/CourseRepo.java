package com.efeakif.intibak_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.efeakif.intibak_control.entity.Course;
import java.util.List;

public interface CourseRepo extends JpaRepository<Course, Integer> {

    @Query("Select c from Course c where c.academicYear=:academicYear and c.courseCode=:courseCode")
    List<Course> findByAcademicYearAndCourseCode(@Param("academicYear") String academicYear,
            @Param("courseCode") String courseCode);

}
