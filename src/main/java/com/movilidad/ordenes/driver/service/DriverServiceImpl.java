package com.movilidad.ordenes.driver.service;

import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.driver.dto.DriverRequest;
import com.movilidad.ordenes.driver.dto.DriverResponse;
import com.movilidad.ordenes.driver.entity.Driver;
import com.movilidad.ordenes.driver.mapper.DriverMapper;
import com.movilidad.ordenes.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;

    @Override
    @Transactional
    public DriverResponse create(DriverRequest request) {
        if (driverRepository.existsByLicenseNumber(request.licenseNumber())) {
            throw new BusinessException("Ya existe un conductor con la licencia " + request.licenseNumber());
        }

        Driver driver = driverRepository.save(driverMapper.toEntity(request));
        log.info("Conductor {} registrado con licencia {}", driver.getId(), driver.getLicenseNumber());
        return driverMapper.toResponse(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DriverResponse> getActiveDrivers() {
        return driverMapper.toResponse(driverRepository.findByActiveTrueOrderByNameAsc());
    }
}