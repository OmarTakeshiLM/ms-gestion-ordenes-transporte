package com.movilidad.ordenes.order.mapper;

import com.movilidad.ordenes.order.dto.OrderRequest;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderRequest request);

    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponse(List<Order> orders);
}