package com.clinic.billing.utils.mapper;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SpecializationMapper {

    public Specialization createSpecializationEntity(CreateSpecializationRequest request) {
        return Specialization.builder()
                .name(request.getName())
                .status(Status.valueOf(request.getStatus()))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public SpecializationResponse createSpecializationResponse(Specialization specialization) {
        return SpecializationResponse.builder()
                .specializationId(specialization.getId())
                .specializationName(specialization.getName())
                .status(specialization.getStatus().name())
                .build();
    }

    public Specialization updateSpecializationEntity(Specialization specialization, CreateSpecializationRequest req) {
        specialization.setName(req.getName());
        specialization.setStatus(Status.valueOf(req.getStatus()));
        specialization.setUpdatedTime(LocalDateTime.now());
        return specialization;
    }
}
