package com.movilidad.ordenes.order.repository;

import com.movilidad.ordenes.order.entity.Order;
import com.movilidad.ordenes.order.entity.OrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class OrderSpecs {

    private OrderSpecs() {
    }

    public static Specification<Order> withStatus(OrderStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Order> originLike(String origin) {
        return (root, query, cb) -> isEmpty(origin) ? null
                : cb.like(cb.lower(root.get("origin")), "%" + origin.toLowerCase() + "%");
    }

    public static Specification<Order> destinationLike(String destination) {
        return (root, query, cb) -> isEmpty(destination) ? null
                : cb.like(cb.lower(root.get("destination")), "%" + destination.toLowerCase() + "%");
    }

    public static Specification<Order> createdBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("createdAt"), from.atStartOfDay(), to.plusDays(1).atStartOfDay());
            }
            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
            }
            if (to != null) {
                return cb.lessThan(root.get("createdAt"), to.plusDays(1).atStartOfDay());
            }
            return null;
        };
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }
}