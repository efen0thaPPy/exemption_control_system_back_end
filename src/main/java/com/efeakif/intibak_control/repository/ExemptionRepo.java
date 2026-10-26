package com.efeakif.intibak_control.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.efeakif.intibak_control.entity.LLMExemptionReport;
import java.util.List;

public interface ExemptionRepo extends JpaRepository<LLMExemptionReport, Integer> {

    @Query("Select e from LLMExemptionReport e join fetch e.selectedCourse where e.selectedCourse.id=:courseId")
    List<LLMExemptionReport> findSelectedCourseByCourseId(@Param("courseId") long courseId);

    void deleteBySelectedCourse_Id(long courseId);

}
