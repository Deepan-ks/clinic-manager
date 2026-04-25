package com.clinic.billing.service;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;

import java.util.List;

public interface MedicalServiceService {

    List<MedicalServiceResponse> getAllActiveServices();

    MedicalServiceResponse createMedicalService(MedicalServiceRequest request);

    MedicalServiceResponse updateMedicalService(Long id, MedicalServiceRequest request);

    void deleteMedicalService(Long id);

    List<MedicalServiceResponse> findBySpecializationById(Long specializationId);
}
