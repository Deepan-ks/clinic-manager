package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.MedicalServiceRepository;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.MedicalServiceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecializationRepository specializationRepository;

    public List<MedicalServiceResponse> getAllActiveServices() {
        return medicalServiceRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MedicalServiceResponse createMedicalService(MedicalServiceRequest request) {

        Specialization specialization = findSpecialization(request.getSpecializationId());

        MedicalService medicalService = MedicalService.builder()
                .name(request.getName())
                .price(request.getPrice())
                .status(request.getStatus())
                .specialization(specialization)
                .description(request.getDescription())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        MedicalService saved = medicalServiceRepository.save(medicalService);
        return mapToResponse(saved);
    }

    @Override
    public MedicalServiceResponse updateMedicalService(Long id, MedicalServiceRequest request) {

        MedicalService existingService = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical Service with id " + id + " not found"));

        if (request.getName() != null) {
            existingService.setName(request.getName());
        }

        if (request.getPrice() != null) {
            existingService.setPrice(request.getPrice());
        }

        if (request.getStatus() != null) {
            existingService.setStatus(request.getStatus());
        }

        existingService.setUpdatedTime(LocalDateTime.now());

        MedicalService saved = medicalServiceRepository.save(existingService);

        return mapToResponse(saved);
    }

    @Override
    public void deleteMedicalService(Long id) {
        MedicalService existingService = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical Service with id " + id + " not found"));

        existingService.setStatus(Status.INACTIVE);
        existingService.setUpdatedTime(LocalDateTime.now());

        medicalServiceRepository.save(existingService);
    }

    @Override
    public List<MedicalServiceResponse> findBySpecializationById(Long specializationId) {
        List<MedicalService> services = medicalServiceRepository.findBySpecializationId(specializationId);
        return services.stream().map(this::mapToResponse).toList();
    }

    private MedicalServiceResponse mapToResponse(MedicalService medicalService) {
        return MedicalServiceResponse.builder()
                .serviceId(medicalService.getId())
                .serviceName(medicalService.getName())
                .price(medicalService.getPrice())
                .build();
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization with id " + id + " not found"));
    }

}
