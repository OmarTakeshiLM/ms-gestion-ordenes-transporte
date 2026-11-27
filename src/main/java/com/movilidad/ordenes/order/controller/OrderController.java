package com.movilidad.ordenes.order.controller;

import com.movilidad.ordenes.order.dto.OrderRequest;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.dto.StatusRequest;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Ordenes")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una orden nueva")
    public OrderResponse create(@Valid @RequestBody OrderRequest request) {
        return orderService.create(request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambia el estado de una orden")
    public OrderResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody StatusRequest request) {
        return orderService.changeStatus(id, request.status());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta una orden por su id")
    public OrderResponse getById(@PathVariable UUID id) {
        return orderService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Lista ordenes con filtros opcionales")
    public List<OrderResponse> search(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return orderService.search(status, origin, destination, from, to);
    }
}