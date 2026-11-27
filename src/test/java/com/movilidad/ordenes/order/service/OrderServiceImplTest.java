package com.movilidad.ordenes.order.service;

import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.common.exception.NotFoundException;
import com.movilidad.ordenes.order.dto.OrderRequest;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.entity.Order;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.mapper.OrderMapper;
import com.movilidad.ordenes.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createsOrderInCreatedStatus() {
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            order.setStatus(OrderStatus.CREATED);
            return order;
        });

        OrderResponse response = orderService.create(new OrderRequest("CDMX", "Monterrey"));

        assertNotNull(response.id());
        assertEquals(OrderStatus.CREATED, response.status());
        assertEquals("CDMX", response.origin());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void changesStatusWhenTransitionIsValid() {
        Order order = order(OrderStatus.CREATED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        OrderResponse response = orderService.changeStatus(order.getId(), OrderStatus.IN_TRANSIT);

        assertEquals(OrderStatus.IN_TRANSIT, response.status());
        verify(orderRepository).saveAndFlush(order);
    }

    @Test
    void rejectsInvalidTransition() {
        Order order = order(OrderStatus.DELIVERED);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        assertThrows(BusinessException.class,
                () -> orderService.changeStatus(order.getId(), OrderStatus.IN_TRANSIT));

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository, never()).saveAndFlush(any());
    }

    @Test
    void throwsNotFoundWhenOrderDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getById(id));
    }

    private Order order(OrderStatus status) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(status);
        order.setOrigin("CDMX");
        order.setDestination("Monterrey");
        return order;
    }
}