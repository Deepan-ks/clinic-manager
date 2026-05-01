package com.clinic.billing.service;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicalServiceService {

    List<MedicalServiceResponse> getAllActiveServices();

    /** Paginated list with optional search and specialization filter */
    Page<MedicalServiceResponse> getServices(String search, Long specializationId, Pageable pageable);

    MedicalServiceResponse createMedicalService(MedicalServiceRequest request);

    MedicalServiceResponse updateMedicalService(Long id, MedicalServiceRequest request);

    void deleteMedicalService(Long id);

    List<MedicalServiceResponse> findBySpecializationById(Long specializationId);
}
