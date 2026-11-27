package com.movilidad.ordenes.assignment.service;

import com.movilidad.ordenes.assignment.dto.AssignmentRequest;
import com.movilidad.ordenes.assignment.dto.AssignmentResponse;
import com.movilidad.ordenes.assignment.entity.Assignment;
import com.movilidad.ordenes.assignment.mapper.AssignmentMapper;
import com.movilidad.ordenes.assignment.repository.AssignmentRepository;
import com.movilidad.ordenes.common.exception.BadRequestException;
import com.movilidad.ordenes.common.exception.BusinessException;
import com.movilidad.ordenes.common.exception.NotFoundException;
import com.movilidad.ordenes.driver.entity.Driver;
import com.movilidad.ordenes.driver.repository.DriverRepository;
import com.movilidad.ordenes.order.entity.Order;
import com.movilidad.ordenes.order.entity.OrderStatus;
import com.movilidad.ordenes.order.repository.OrderRepository;
import com.movilidad.ordenes.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {

    private static final Set<String> PDF_TYPES = Set.of("application/pdf");
    private static final Set<String> PDF_EXTENSIONS = Set.of("pdf");
    private static final Set<String> IMAGE_TYPES = Set.of("image/png", "image/jpeg");
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("png", "jpg", "jpeg");

    private final AssignmentRepository assignmentRepository;
    private final OrderRepository orderRepository;
    private final DriverRepository driverRepository;
    private final FileStorageService fileStorageService;
    private final AssignmentMapper assignmentMapper;

    @Override
    @Transactional
    public AssignmentResponse assign(AssignmentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new NotFoundException("No existe la orden " + request.orderId()));
        Driver driver = driverRepository.findById(request.driverId())
                .orElseThrow(() -> new NotFoundException("No existe el conductor " + request.driverId()));

        if (!driver.isActive()) {
            throw new BusinessException("El conductor " + driver.getName() + " no está activo");
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException("Solo se pueden asignar órdenes en estado CREATED, esta orden está en " + order.getStatus());
        }
        if (assignmentRepository.existsByOrderId(order.getId())) {
            throw new BusinessException("La orden " + order.getId() + " ya tiene un conductor asignado");
        }

        Assignment assignment = new Assignment();
        assignment.setOrder(order);
        assignment.setDriver(driver);
        assignmentRepository.save(assignment);

        log.info("Conductor {} asignado a la orden {}", driver.getId(), order.getId());
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional
    public AssignmentResponse attachDocument(UUID id, MultipartFile file) {
        validateFile(file, PDF_TYPES, PDF_EXTENSIONS, "un PDF");
        Assignment assignment = findAssignment(id);

        String oldPath = assignment.getDocumentPath();
        assignment.setDocumentPath(fileStorageService.store(file, "assignments/" + id));
        assignment.setDocumentName(file.getOriginalFilename());
        fileStorageService.delete(oldPath);

        log.info("PDF {} agregado a la asignación {}", file.getOriginalFilename(), id);
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional
    public AssignmentResponse attachImage(UUID id, MultipartFile file) {
        validateFile(file, IMAGE_TYPES, IMAGE_EXTENSIONS, "una imagen PNG o JPG");
        Assignment assignment = findAssignment(id);

        String oldPath = assignment.getImagePath();
        assignment.setImagePath(fileStorageService.store(file, "assignments/" + id));
        assignment.setImageName(file.getOriginalFilename());
        fileStorageService.delete(oldPath);

        log.info("Imagen {} agregada a la asignación {}", file.getOriginalFilename(), id);
        return assignmentMapper.toResponse(assignment);
    }

    @Override
    @Transactional(readOnly = true)
    public AssignmentResponse getById(UUID id) {
        return assignmentMapper.toResponse(findAssignment(id));
    }

    private Assignment findAssignment(UUID id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la asignación " + id));
    }

    private void validateFile(MultipartFile file, Set<String> types, Set<String> extensions, String expected) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo está vacío");
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        boolean validExtension = extension != null && extensions.contains(extension.toLowerCase());
        boolean validType = file.getContentType() != null && types.contains(file.getContentType());

        if (!validExtension || !validType) {
            throw new BadRequestException("El archivo debe ser " + expected);
        }
    }
}