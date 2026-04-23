package com.clinic.billing.service;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.entity.Doctor;
import jakarta.validation.Valid;

import java.util.List;

public interface DoctorService {

    DoctorResponse createDoctor(CreateDoctorRequest req);

    List<DoctorResponse> getAllDoctor(Long specializationId);

    DoctorResponse getByDoctorId(Long id);

    DoctorResponse updateDoctor(Long id, CreateDoctorRequest req);

    void deleteDoctor(Long id);
}
