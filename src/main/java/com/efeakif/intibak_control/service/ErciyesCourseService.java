package com.efeakif.intibak_control.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.efeakif.intibak_control.dto.response.ErciyesCourseResponseDto;
import com.efeakif.intibak_control.entity.ErciyesCourse;
import com.efeakif.intibak_control.enums.TransferType;
import com.efeakif.intibak_control.mapping.Mapper;
import com.efeakif.intibak_control.repository.ErciyesCourseRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ErciyesCourseService {

    private final ErciyesCourseRepo erciyesRepo;
    private final Mapper mapper;

    public List<ErciyesCourseResponseDto> findByMaxAcademicYear(TransferType transferType) {

        if (TransferType.VERTICAL_TRANSFER == transferType) {
            List<ErciyesCourse> courses = erciyesRepo.findByMaxAcademicYearForVertical();
            List<ErciyesCourseResponseDto> dtos = courses.stream().map(x -> mapper.toDto(x)).toList();
            return dtos;
        } else {
            List<ErciyesCourse> courses = erciyesRepo.findByMaxAcademicYear();
            List<ErciyesCourseResponseDto> dtos = courses.stream().map(x -> mapper.toDto(x)).toList();
            return dtos;
        }

    }

    public String findMaxAcademicYear() {
        return erciyesRepo.findMaxAcademicYear();

    }

    public List<ErciyesCourseResponseDto> findByAcademicYear(String year) {

        List<ErciyesCourse> courses = erciyesRepo.findByAcademicYear(year);

        return courses.stream().map(x -> mapper.toDto(x)).toList();

    }

}
