package com.movilidad.ordenes.order.service;

import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.common.exception.NotFoundException;
import com.movilidad.ordenes.order.dto.OrderRequest;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.entity.Order;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.mapper.OrderMapper;
import com.movilidad.ordenes.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.movilidad.ordenes.order.repository.OrderSpecs.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = orderRepository.save(orderMapper.toEntity(request));
        log.info("Orden {} creada: {} -> {}", order.getId(), order.getOrigin(), order.getDestination());
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse changeStatus(UUID id, OrderStatus status) {
        Order order = findOrder(id);
        OrderStatus current = order.getStatus();

        if (!current.canChangeTo(status)) {
            throw new BusinessException("No se puede cambiar la orden de " + current + " a " + status);
        }

        order.setStatus(status);
        orderRepository.saveAndFlush(order);
        log.info("Orden {} pasó de {} a {}", id, current, status);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        return orderMapper.toResponse(findOrder(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> search(OrderStatus status, String origin, String destination,
                                      LocalDate from, LocalDate to) {
        Specification<Order> filters = Specification.allOf(
                withStatus(status),
                originLike(origin),
                destinationLike(destination),
                createdBetween(from, to)
        );
        List<Order> orders = orderRepository.findAll(filters, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderMapper.toResponse(orders);
    }

    private Order findOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la orden " + id));
    }
}