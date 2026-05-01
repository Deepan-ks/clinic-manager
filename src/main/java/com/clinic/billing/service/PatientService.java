package com.clinic.billing.service;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest request);

    PatientResponse getPatient(Long id);

    /** Paginated list with optional search (name / phone) */
    Page<PatientResponse> getPatients(String search, Pageable pageable);

    List<PatientResponse> getAllPatients();

    List<PatientResponse> searchPatient(String query);

    PatientResponse updatePatient(Long id, UpdatePatientRequest request);
}
