package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.entity.Doctor;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.DoctorRepository;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public DoctorResponse createDoctor(CreateDoctorRequest req) {

        Specialization spec = findSpecialization(req.getSpecializationId());

        Doctor doctor = Doctor.builder()
                .name(req.getName())
                .specialization(spec)
                .phone(req.getPhone())
                .status(Status.valueOf(req.getStatus()))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    public List<DoctorResponse> getAllDoctorBySpecialization(Long specializationId) {

        List<Doctor> doctors = (specializationId != null)
                ? doctorRepository.findBySpecializationId(specializationId)
                : doctorRepository.findAll();

        return doctors.stream()
                .filter(doctor -> doctor.getStatus() == Status.ACTIVE)
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public DoctorResponse getByDoctorId(Long id) {
        return mapToResponse(findDoctor(id));
    }

    @Override
    public DoctorResponse updateDoctor(Long id, CreateDoctorRequest req) {

        Doctor doctor = findDoctor(id);
        Specialization spec = findSpecialization(req.getSpecializationId());

        doctor.setName(req.getName());
        doctor.setPhone(req.getPhone());
        doctor.setSpecialization(spec);
        doctor.setStatus(Status.valueOf(req.getStatus()));
        doctor.setUpdatedTime(LocalDateTime.now());

        return mapToResponse(doctorRepository.save(doctor));
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctor(id);

        // soft delete
        doctor.setStatus(Status.INACTIVE);
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorResponse> findAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream().map(this::mapToResponse).toList();
    }

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));
    }

    private DoctorResponse mapToResponse(Doctor d) {
        return DoctorResponse.builder()
                .doctorId(d.getId())
                .doctorName(d.getName())
                .phone(d.getPhone())
                .status(d.getStatus().name())
                .specializationId(d.getSpecialization().getId())
                .specializationName(d.getSpecialization().getName())
                .build();
    }
}
