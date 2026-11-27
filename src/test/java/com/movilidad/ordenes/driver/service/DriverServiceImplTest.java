package com.movilidad.ordenes.driver.service;

import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.driver.dto.DriverRequest;
import com.movilidad.ordenes.driver.dto.DriverResponse;
import com.movilidad.ordenes.driver.entity.Driver;
import com.movilidad.ordenes.driver.mapper.DriverMapper;
import com.movilidad.ordenes.driver.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @Spy
    private DriverMapper driverMapper = Mappers.getMapper(DriverMapper.class);

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    void createsActiveDriverByDefault() {
        when(driverRepository.existsByLicenseNumber("LIC-001")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(invocation -> {
            Driver driver = invocation.getArgument(0);
            driver.setId(UUID.randomUUID());
            return driver;
        });

        DriverResponse response = driverService.create(new DriverRequest("Juan Pérez", "LIC-001", null));

        assertNotNull(response.id());
        assertTrue(response.active());
    }

    @Test
    void rejectsDuplicatedLicense() {
        when(driverRepository.existsByLicenseNumber("LIC-001")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> driverService.create(new DriverRequest("Juan Pérez", "LIC-001", true)));

        verify(driverRepository, never()).save(any());
    }

    @Test
    void returnsOnlyActiveDrivers() {
        Driver driver = new Driver();
        driver.setId(UUID.randomUUID());
        driver.setName("Juan Pérez");
        driver.setLicenseNumber("LIC-001");
        when(driverRepository.findByActiveTrueOrderByNameAsc()).thenReturn(List.of(driver));

        List<DriverResponse> drivers = driverService.getActiveDrivers();

        assertEquals(1, drivers.size());
        assertEquals("Juan Pérez", drivers.get(0).name());
    }
}