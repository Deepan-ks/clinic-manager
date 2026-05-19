package com.clinic.billing.utils.mapper;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.entity.enums.Gender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PatientMapper {

    public Patient createPatientEntity(CreatePatientRequest request) {
        return Patient.builder()
                .name(request.getName())
                .age(request.getAge())
                .address(request.getAddress())
                .gender(Gender.valueOf(request.getGender()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    public Patient updatePatientEntity(Patient existingPatient, UpdatePatientRequest request) {
        existingPatient.setPhone(request.getPhone());
        existingPatient.setEmail(request.getEmail());
        existingPatient.setAddress(request.getAddress());
        existingPatient.setUpdatedTime(LocalDateTime.now());
        return existingPatient;
    }

    public PatientResponse createPatientResponse(Patient patient) {
        return PatientResponse.builder().patientId(patient.getId())
                .patientName(patient.getName())
                .gender(String.valueOf(patient.getGender()))
                .age(patient.getAge())
                .address(patient.getAddress())
                .patientPhone(patient.getPhone())
                .email(patient.getEmail())
                .createdDate(patient.getCreatedTime())
                .updatedDate(patient.getUpdatedTime())
                .build();
    }
}
