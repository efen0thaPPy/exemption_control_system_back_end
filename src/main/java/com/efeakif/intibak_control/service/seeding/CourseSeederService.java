package com.efeakif.intibak_control.service.seeding;

import java.util.List;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.entity.Course;
import com.efeakif.intibak_control.repository.CourseRepo;
import com.efeakif.intibak_control.service.VectorStoreService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseSeederService {
    private final ErciyesScrapingSeedingService erciyesScrapingService;
    private final VectorStoreService vectorStoreService;
    private final CourseRepo courseRepo;

    @Transactional
    public void seed(String academicYear) {
        erciyesScrapingService.scrapeAndSeedtheCirriculum(academicYear);
        List<Course> courses = courseRepo.findByAcademicYear(academicYear);
        vectorStoreService.saveVectors(courses);

    }

}
