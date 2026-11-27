package com.movilidad.ordenes.driver.controller;

import com.movilidad.ordenes.driver.dto.DriverRequest;
import com.movilidad.ordenes.driver.dto.DriverResponse;
import com.movilidad.ordenes.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Conductores")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un conductor")
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return driverService.create(request);
    }

    @GetMapping
    @Operation(summary = "Lista los conductores activos")
    public List<DriverResponse> getActiveDrivers() {
        return driverService.getActiveDrivers();
    }
}