package com.movilidad.ordenes.order.controller;

import com.movilidad.ordenes.common.exception.NotFoundException;
import com.movilidad.ordenes.order.dto.OrderResponse;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void createsOrder() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        when(orderService.create(any()))
                .thenReturn(new OrderResponse(id, OrderStatus.CREATED, "CDMX", "Monterrey", now, now));

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"origin": "CDMX", "destination": "Monterrey"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    void returnsBadRequestWhenFieldsAreEmpty() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"origin": "", "destination": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.origin").exists())
                .andExpect(jsonPath("$.errors.destination").exists());
    }

    @Test
    void returnsNotFoundWhenOrderDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getById(id)).thenThrow(new NotFoundException("No existe la orden " + id));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound());
    }
}