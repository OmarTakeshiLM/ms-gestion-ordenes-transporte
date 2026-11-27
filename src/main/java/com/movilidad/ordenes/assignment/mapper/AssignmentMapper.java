package com.movilidad.ordenes.assignment.mapper;

import com.movilidad.ordenes.assignment.dto.AssignmentResponse;
import com.movilidad.ordenes.assignment.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "orderStatus", source = "order.status")
    @Mapping(target = "driverId", source = "driver.id")
    @Mapping(target = "driverName", source = "driver.name")
    AssignmentResponse toResponse(Assignment assignment);
}