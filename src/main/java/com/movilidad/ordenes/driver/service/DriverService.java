package com.movilidad.ordenes.driver.service;

import com.movilidad.ordenes.driver.dto.DriverRequest;
import com.movilidad.ordenes.driver.dto.DriverResponse;

import java.util.List;

public interface DriverService {

    DriverResponse create(DriverRequest request);

    List<DriverResponse> getActiveDrivers();
}