package com.movilidad.ordenes.assignment.repository;

import com.movilidad.ordenes.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    boolean existsByOrderId(UUID orderId);
}