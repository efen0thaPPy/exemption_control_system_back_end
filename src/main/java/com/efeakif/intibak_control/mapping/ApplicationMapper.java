package com.efeakif.intibak_control.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import com.efeakif.intibak_control.dto.request.SelectedCourseRequestDto;
import com.efeakif.intibak_control.dto.response.ApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.SelectedCourseResponse;
import com.efeakif.intibak_control.dto.response.SimpleApplicationResponseDto;
import com.efeakif.intibak_control.entity.Application;
import com.efeakif.intibak_control.entity.SelectedCourse;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationResponseDto toApplicationResponseDto(Application application);

    @Mapping(target = ".", source = "student")
    SimpleApplicationResponseDto toSimpleDto(Application application);

    @Mapping(target = ".", source = "courseExemptionReport")
    SelectedCourseResponse toSelectedCourseResponseDto(SelectedCourse selectedCourse);

    @Mapping(target = "application", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "courseExemptionReport.exemptionStatus", constant = "PENDING")
    @Mapping(target = "courseExemptionReport.targetCourseCode", source = "selectedCourseRequestDto.targetCourseCode")
    SelectedCourse toSelectedCourseEntity(SelectedCourseRequestDto selectedCourseRequestDto,
            String coursePdfPath);

}
