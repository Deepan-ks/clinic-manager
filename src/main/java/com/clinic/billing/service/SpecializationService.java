package com.clinic.billing.service;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;

import java.util.List;

public interface SpecializationService {

    SpecializationResponse createSpecialization(CreateSpecializationRequest req);

    SpecializationResponse getBySpecializationId(Long id);

    List<SpecializationResponse> getAllSpecialization();

    SpecializationResponse updateSpecialization(Long id, CreateSpecializationRequest req);

    void deleteSpecialization(Long id);

}
