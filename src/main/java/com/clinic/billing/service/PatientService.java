package com.clinic.billing.service;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {

    PatientResponse createPatient(CreatePatientRequest request);

    PatientResponse getPatient(Long id);

    List<PatientResponse> getAllPatients();

    List<PatientResponse> searchPatient(String query);

    PatientResponse updatePatient(Long id, UpdatePatientRequest request);
}
