package com.clinic.billing.service;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.entity.enums.Gender;
import com.clinic.billing.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = Patient.builder()
                .name(request.getName())
                .age(request.getAge())
                .gender(Gender.valueOf(request.getGender()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        return mapToPatientResponse(savedPatient);
    }

    @Override
    public PatientResponse getPatient(Long id) {
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() -> new RuntimeException("patient not found"));
        return mapToPatientResponse(existingPatient);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(this::mapToPatientResponse).toList();
    }

    @Override
    public List<PatientResponse> searchPatient(String query) {
        return patientRepository.searchPatients(query)
                .stream()
                .map(this::mapToPatientResponse)
                .toList();
    }

    @Override
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request) {
        Patient existingPatient =  patientRepository.findById(id).orElseThrow(() -> new RuntimeException("patient not found"));

        if(request.getPhone() != null){
            existingPatient.setPhone(request.getPhone());
        }
        if(request.getEmail() != null){
            existingPatient.setEmail(request.getEmail());
        }
        patientRepository.save(existingPatient);
        return mapToPatientResponse(existingPatient);
    }


    private PatientResponse mapToPatientResponse(Patient patient) {
        return PatientResponse.builder().patientId(patient.getId())
                .patientName(patient.getName())
                .gender(String.valueOf(patient.getGender()))
                .age(patient.getAge())
                .patientPhone(patient.getPhone())
                .build();
    }
}
