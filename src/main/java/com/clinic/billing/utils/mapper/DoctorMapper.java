package com.clinic.billing.utils.mapper;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.entity.Doctor;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DoctorMapper {

    public Doctor createDoctorEntity(CreateDoctorRequest req, Specialization specialization) {
        return Doctor.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .specialization(specialization)
                .status(Status.valueOf(req.getStatus()))
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public DoctorResponse createDoctorResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .doctorId(doctor.getId())
                .doctorName(doctor.getName())
                .phone(doctor.getPhone())
                .status(doctor.getStatus().name())
                .specializationId(doctor.getSpecialization().getId())
                .specializationName(doctor.getSpecialization().getName())
                .build();

    }

    public Doctor updateDoctorEntity(CreateDoctorRequest req, Doctor doctor, Specialization specialization) {
        doctor.setName(req.getName());
        doctor.setPhone(req.getPhone());
        doctor.setSpecialization(specialization);
        doctor.setStatus(Status.valueOf(req.getStatus()));
        doctor.setUpdatedTime(LocalDateTime.now());
        return doctor;
    }
}
