package com.efeakif.intibak_control.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.efeakif.intibak_control.dto.response.ApplicationResponseDto;
import com.efeakif.intibak_control.dto.response.SimpleApplicationResponseDto;
import com.efeakif.intibak_control.entity.Application;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationResponseDto toDto(Application application);

    @Mapping(target = ".", source = "student")
    SimpleApplicationResponseDto toSimpleDto(Application application);

}
