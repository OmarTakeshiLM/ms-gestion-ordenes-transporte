package com.movilidad.ordenes.assignment.controller;

import com.movilidad.ordenes.assignment.dto.AssignmentRequest;
import com.movilidad.ordenes.assignment.dto.AssignmentResponse;
import com.movilidad.ordenes.assignment.service.AssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "Asignaciones")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Asigna un conductor activo a una orden en estado CREATED")
    public AssignmentResponse assign(@Valid @RequestBody AssignmentRequest request) {
        return assignmentService.assign(request);
    }

    @PostMapping(value = "/{id}/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Adjunta un PDF a la asignación")
    public AssignmentResponse attachDocument(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return assignmentService.attachDocument(id, file);
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Adjunta una imagen PNG o JPG a la asignación")
    public AssignmentResponse attachImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return assignmentService.attachImage(id, file);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta una asignación")
    public AssignmentResponse getById(@PathVariable UUID id) {
        return assignmentService.getById(id);
    }
}