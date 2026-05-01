package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;

    @Override
    public SpecializationResponse createSpecialization(CreateSpecializationRequest req) {
        Specialization s = Specialization.builder()
                .name(req.getName())
                .status(Status.valueOf(req.getStatus()))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        return mapToResponse(specializationRepository.save(s));
    }

    @Override
    public List<SpecializationResponse> getAllSpecialization() {
        return specializationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SpecializationResponse getBySpecializationId(Long id) {
        return mapToResponse(find(id));
    }

    @Override
    public SpecializationResponse updateSpecialization(Long id, CreateSpecializationRequest req) {
        Specialization s = find(id);
        s.setName(req.getName());
        s.setStatus(Status.valueOf(req.getStatus()));
        s.setUpdatedTime(LocalDateTime.now());

        return mapToResponse(specializationRepository.save(s));
    }

    @Override
    public void deleteSpecialization(Long id) {
        Specialization s = find(id);
        // soft delete
        s.setStatus(Status.INACTIVE);
        specializationRepository.save(s);
    }

    private Specialization find(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));
    }

    private SpecializationResponse mapToResponse(Specialization s) {
        return SpecializationResponse.builder()
                .specializationId(s.getId())
                .specializationName(s.getName())
                .status(s.getStatus().name())
                .build();
    }

}
