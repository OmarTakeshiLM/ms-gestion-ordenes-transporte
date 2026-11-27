package com.movilidad.ordenes.order.service;

import com.movilidad.ordenes.order.dto.OrderRequest;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.entity.OrderStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderRequest request);

    OrderResponse changeStatus(UUID id, OrderStatus status);

    OrderResponse getById(UUID id);

    List<OrderResponse> search(OrderStatus status, String origin, String destination, LocalDate from, LocalDate to);
}