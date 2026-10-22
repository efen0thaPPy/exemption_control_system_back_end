package com.efeakif.intibak_control.service.seeding;

import java.util.List;

import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.entity.ErciyesCourse;

import com.efeakif.intibak_control.repository.ErciyesCourseRepo;
import com.efeakif.intibak_control.service.ai.VectorStoreService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErciyesCourseSeederService {
    private final ErciyesScrapingSeedingService erciyesScrapingService;
    private final VectorStoreService vectorStoreService;
    private final ErciyesCourseRepo erciyesCourseRepo;

    @Transactional
    public void seed(String academicYear) {
        erciyesScrapingService.scrapeAndSeedtheCirriculum(academicYear);
        List<ErciyesCourse> courses = erciyesCourseRepo.findByAcademicYear(academicYear);
        vectorStoreService.saveVectors(courses);

    }

}
