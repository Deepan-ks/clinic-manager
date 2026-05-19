package com.clinic.billing.utils.mapper;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Specialization;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MedicalServiceMapper {

    public MedicalService createMedicalServiceEntity(MedicalServiceRequest request, Specialization specialization) {
        return MedicalService.builder()
                .name(request.getName())
                .price(request.getPrice())
                .status(request.getStatus())
                .specialization(specialization)
                .description(request.getDescription())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public MedicalServiceResponse createMedicalServiceResponse(MedicalService medicalService) {
        return MedicalServiceResponse.builder()
                .serviceId(medicalService.getId())
                .serviceName(medicalService.getName())
                .description(medicalService.getDescription())
                .price(medicalService.getPrice())
                .status(medicalService.getStatus().name())
                .specializationId(medicalService.getSpecialization().getId())
                .build();
    }

    public MedicalService updateMedicalServiceEntity(MedicalService existingService, MedicalServiceRequest request) {
        existingService.setName(request.getName());
        existingService.setPrice(request.getPrice());
        existingService.setStatus(request.getStatus());
        existingService.setUpdatedTime(LocalDateTime.now());

        return existingService;
    }
}
