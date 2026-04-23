package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.repository.MedicalServiceRepository;
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

    public List<MedicalServiceResponse> getAllActiveServices() {
        return medicalServiceRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MedicalServiceResponse createMedicalService(MedicalServiceRequest request) {
        MedicalService medicalService = MedicalService.builder()
                .name(request.getName())
                .price(request.getPrice())
                .gstPercentage(request.getGstPercentage())
                .isActive(request.getIsActive())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
        MedicalService saved = medicalServiceRepository.save(medicalService);
        return mapToResponse(saved);
    }

    @Override
    public MedicalServiceResponse updateMedicalService(Long id, MedicalServiceRequest request) {

        MedicalService existingService = medicalServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical Service with id " + id + " not found"));

        if (request.getName() != null) {
            existingService.setName(request.getName());
        }

        if (request.getPrice() != null) {
            existingService.setPrice(request.getPrice());
        }

        if (request.getGstPercentage() != null) {
            existingService.setGstPercentage(request.getGstPercentage());
        }

        if (request.getIsActive() != null) {
            existingService.setIsActive(request.getIsActive());
        }

        existingService.setUpdatedTime(LocalDateTime.now());

        MedicalService saved = medicalServiceRepository.save(existingService);

        return mapToResponse(saved);
    }

    @Override
    public void deleteMedicalService(Long id) {
       MedicalService existingService = medicalServiceRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Medical Service with id " + id + " not found"));

       existingService.setIsActive(false);
       existingService.setUpdatedTime(LocalDateTime.now());

       medicalServiceRepository.save(existingService);
    }

    private MedicalServiceResponse mapToResponse(MedicalService medicalService) {
        return MedicalServiceResponse.builder()
                .serviceId(medicalService.getId())
                .serviceName(medicalService.getName())
                .gstPercentage(medicalService.getGstPercentage())
                .price(medicalService.getPrice())
                .build();
    }


}
