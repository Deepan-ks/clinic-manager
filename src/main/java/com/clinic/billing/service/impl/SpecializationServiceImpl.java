package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;


    public SpecializationResponse createSpecialization(CreateSpecializationRequest req) {
        Specialization s = Specialization.builder()
                .name(req.getName())
                .status(Boolean.TRUE.equals(req.getIsActive()) ? "ACTIVE" : "INACTIVE")
                .build();

        return mapToResponse(specializationRepository.save(s));
    }

    public List<SpecializationResponse> getAllSpecialization() {
        return specializationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SpecializationResponse getBySpecializationId(Long id) {
        return mapToResponse(find(id));
    }

    public SpecializationResponse updateSpecialization(Long id, CreateSpecializationRequest req) {
        Specialization s = find(id);
        s.setName(req.getName());
        s.setStatus(Boolean.TRUE.equals(req.getIsActive()) ? "ACTIVE" : "INACTIVE");

        return mapToResponse(specializationRepository.save(s));
    }

    public void deleteSpecialization(Long id) {
        Specialization s = find(id);
        // soft delete
        s.setStatus("INACTIVE");
        specializationRepository.save(s);
    }

    private Specialization find(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Specialization not found"));
    }

    private SpecializationResponse mapToResponse(Specialization s) {
        return SpecializationResponse.builder()
                .specializationId(s.getId())
                .specializationName(s.getName())
                .build();
    }

}
