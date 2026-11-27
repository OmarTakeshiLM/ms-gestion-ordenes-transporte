package com.movilidad.ordenes.assignment.service;

import com.movilidad.ordenes.assignment.dto.AssignmentRequest;
import com.movilidad.ordenes.assignment.dto.AssignmentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AssignmentService {

    AssignmentResponse assign(AssignmentRequest request);

    AssignmentResponse attachDocument(UUID id, MultipartFile file);

    AssignmentResponse attachImage(UUID id, MultipartFile file);

    AssignmentResponse getById(UUID id);
}