package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.SpecializationService;
import com.clinic.billing.utils.mapper.SpecializationMapper;
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

    private final SpecializationMapper specializationMapper;

    @Override
    public SpecializationResponse createSpecialization(CreateSpecializationRequest req) {
        Specialization s = specializationMapper.createSpecializationEntity(req);
        return specializationMapper.createSpecializationResponse(specializationRepository.save(s));
    }

    @Override
    public List<SpecializationResponse> getAllSpecialization() {
        return specializationRepository.findAll()
                .stream()
                .map(specializationMapper::createSpecializationResponse)
                .toList();
    }

    @Override
    public SpecializationResponse getBySpecializationId(Long id) {
        return specializationMapper.createSpecializationResponse(findSpecializationById(id));
    }

    @Override
    public SpecializationResponse updateSpecialization(Long id, CreateSpecializationRequest req) {
        Specialization specialization = findSpecializationById(id);
        Specialization updatedSpecialization = specializationMapper.updateSpecializationEntity(specialization, req);

        return specializationMapper.createSpecializationResponse(specializationRepository.save(updatedSpecialization));
    }

    @Override
    public void deleteSpecialization(Long id) {
        Specialization specialization = findSpecializationById(id);
        // soft delete
        specialization.setStatus(Status.INACTIVE);
        specialization.setUpdatedTime(LocalDateTime.now());

        specializationRepository.save(specialization);
    }

    private Specialization findSpecializationById(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));
    }
}
