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
import com.clinic.billing.utils.Constants;
import com.clinic.billing.utils.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    private final DoctorMapper doctorMapper;

    @Override
    public DoctorResponse createDoctor(CreateDoctorRequest req) {
        Specialization spec = findSpecialization(req.getSpecializationId());
        Doctor doctor = doctorMapper.createDoctorEntity(req, spec);
        return doctorMapper.createDoctorResponse(doctorRepository.save(doctor));
    }

//    TODO: implement findBySpecializationIdAndStatus
    @Override
    public List<DoctorResponse> getAllDoctorBySpecialization(Long specializationId) {
        List<Doctor> doctors = findDoctorsBySpecialization(specializationId);
        return doctors.stream()
                .filter(doctor -> doctor.getStatus() == Status.ACTIVE)
                .map(doctorMapper::createDoctorResponse)
                .toList();
    }

    @Override
    public DoctorResponse getByDoctorId(Long id) {
        return doctorMapper.createDoctorResponse(findDoctor(id));
    }

    @Override
    public DoctorResponse updateDoctor(Long id, CreateDoctorRequest req) {
        Doctor doctor = findDoctor(id);
        Specialization spec = findSpecialization(req.getSpecializationId());

        Doctor updatedDoctor = doctorMapper.updateDoctorEntity(req, doctor, spec);

        return doctorMapper.createDoctorResponse(doctorRepository.save(updatedDoctor));
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctor(id);

        // soft delete
        doctor.setStatus(Status.INACTIVE);
        doctor.setUpdatedTime(LocalDateTime.now());
        doctorRepository.save(doctor);
    }

    @Override
    public List<DoctorResponse> findAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream().map(doctorMapper::createDoctorResponse).toList();
    }

    /* ================== REPOSITORY CALLS ================== */

    private Doctor findDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));
    }

    private List<Doctor> findDoctorsBySpecialization(Long specializationId) {
        return (specializationId != null)
                ? doctorRepository.findBySpecializationId(specializationId)
                : doctorRepository.findAll();
    }

}
