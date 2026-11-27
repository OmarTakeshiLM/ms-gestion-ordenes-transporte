package com.movilidad.ordenes.order.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderStatusTest {

    @ParameterizedTest(name = "{0} -> {1} = {2}")
    @CsvSource({
            "CREATED, IN_TRANSIT, true",
            "CREATED, CANCELLED, true",
            "CREATED, DELIVERED, false",
            "IN_TRANSIT, DELIVERED, true",
            "IN_TRANSIT, CANCELLED, true",
            "IN_TRANSIT, CREATED, false",
            "DELIVERED, CANCELLED, false",
            "DELIVERED, IN_TRANSIT, false",
            "CANCELLED, CREATED, false",
            "CANCELLED, IN_TRANSIT, false"
    })
    void validatesTransitions(OrderStatus from, OrderStatus to, boolean expected) {
        assertEquals(expected, from.canChangeTo(to));
    }
}