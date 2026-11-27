package com.movilidad.ordenes.assignment.service;

import com.movilidad.ordenes.assignment.dto.AssignmentRequest;
import com.movilidad.ordenes.assignment.dto.AssignmentResponse;
import com.movilidad.ordenes.assignment.entity.Assignment;
import com.movilidad.ordenes.assignment.mapper.AssignmentMapper;
import com.movilidad.ordenes.assignment.repository.AssignmentRepository;
import com.movilidad.ordenes.common.exception.BadRequestException;
import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.driver.entity.Driver;
import com.movilidad.ordenes.driver.repository.DriverRepository;
import com.movilidad.ordenes.order.entity.Order;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.repository.OrderRepository;
import com.movilidad.ordenes.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentServiceImplTest {

    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private FileStorageService fileStorageService;

    @Spy
    private AssignmentMapper assignmentMapper = Mappers.getMapper(AssignmentMapper.class);

    @InjectMocks
    private AssignmentServiceImpl assignmentService;

    @Test
    void assignsActiveDriverToCreatedOrder() {
        Order order = order(OrderStatus.CREATED);
        Driver driver = driver(true);
        mockFind(order, driver);
        when(assignmentRepository.existsByOrderId(order.getId())).thenReturn(false);

        AssignmentResponse response = assignmentService.assign(new AssignmentRequest(order.getId(), driver.getId()));

        assertEquals(order.getId(), response.orderId());
        assertEquals(driver.getId(), response.driverId());
        verify(assignmentRepository).save(any(Assignment.class));
    }

    @Test
    void rejectsInactiveDriver() {
        Order order = order(OrderStatus.CREATED);
        Driver driver = driver(false);
        mockFind(order, driver);

        assertThrows(BusinessException.class,
                () -> assignmentService.assign(new AssignmentRequest(order.getId(), driver.getId())));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void rejectsOrderThatIsNotCreated() {
        Order order = order(OrderStatus.IN_TRANSIT);
        Driver driver = driver(true);
        mockFind(order, driver);

        assertThrows(BusinessException.class,
                () -> assignmentService.assign(new AssignmentRequest(order.getId(), driver.getId())));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void rejectsOrderAlreadyAssigned() {
        Order order = order(OrderStatus.CREATED);
        Driver driver = driver(true);
        mockFind(order, driver);
        when(assignmentRepository.existsByOrderId(order.getId())).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> assignmentService.assign(new AssignmentRequest(order.getId(), driver.getId())));
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    void attachesPdfDocument() {
        Assignment assignment = assignment();
        when(assignmentRepository.findById(assignment.getId())).thenReturn(Optional.of(assignment));
        when(fileStorageService.store(any(), anyString())).thenReturn("assignments/doc.pdf");
        var file = new MockMultipartFile("file", "guia.pdf", "application/pdf", "contenido".getBytes());

        AssignmentResponse response = assignmentService.attachDocument(assignment.getId(), file);

        assertEquals("guia.pdf", response.documentName());
        assertEquals("assignments/doc.pdf", assignment.getDocumentPath());
    }

    @Test
    void rejectsDocumentThatIsNotPdf() {
        var file = new MockMultipartFile("file", "foto.png", "image/png", "contenido".getBytes());

        assertThrows(BadRequestException.class,
                () -> assignmentService.attachDocument(UUID.randomUUID(), file));
        verifyNoInteractions(fileStorageService);
    }

    @Test
    void rejectsEmptyImage() {
        var file = new MockMultipartFile("file", "foto.png", "image/png", new byte[0]);

        assertThrows(BadRequestException.class,
                () -> assignmentService.attachImage(UUID.randomUUID(), file));
        verifyNoInteractions(fileStorageService);
    }

    private void mockFind(Order order, Driver driver) {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
    }

    private Order order(OrderStatus status) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(status);
        return order;
    }

    private Driver driver(boolean active) {
        Driver driver = new Driver();
        driver.setId(UUID.randomUUID());
        driver.setName("Juan Pérez");
        driver.setActive(active);
        return driver;
    }

    private Assignment assignment() {
        Assignment assignment = new Assignment();
        assignment.setId(UUID.randomUUID());
        assignment.setOrder(order(OrderStatus.CREATED));
        assignment.setDriver(driver(true));
        return assignment;
    }
}