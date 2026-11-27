package com.movilidad.ordenes.driver.mapper;

import com.movilidad.ordenes.driver.dto.DriverRequest;
import com.movilidad.ordenes.driver.dto.DriverResponse;
import com.movilidad.ordenes.driver.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", expression = "java(request.active() == null || request.active())")
    Driver toEntity(DriverRequest request);

    DriverResponse toResponse(Driver driver);

    List<DriverResponse> toResponse(List<Driver> drivers);
}