package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.PatientRepository;
import com.clinic.billing.service.PatientService;
import com.clinic.billing.utils.mapper.PatientMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.clinic.billing.utils.Constants.PATIENT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    private final PatientMapper patientMapper;

    @Override
    public PatientResponse createPatient(CreatePatientRequest request) {
        Patient patient = patientMapper.createPatientEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.createPatientResponse(savedPatient);
    }

    @Override
    public PatientResponse getPatient(Long id) {
        Patient existingPatient = findPatientById(id);
        return patientMapper.createPatientResponse(existingPatient);
    }

    @Override
    public Page<PatientResponse> getPatients(String search, Pageable pageable) {
        String q = (search == null) ? "" : search.trim();
        return patientRepository.findAllPaged(q, pageable).map(patientMapper::createPatientResponse);
    }

    @Override
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream().map(patientMapper::createPatientResponse).toList();
    }

    @Override
    public List<PatientResponse> searchPatient(String query) {
        return patientRepository.searchPatients(query)
                .stream()
                .map(patientMapper::createPatientResponse)
                .toList();
    }

    @Override
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request) {
        Patient existingPatient = findPatientById(id);

        Patient updatedPatient = patientMapper.updatePatientEntity(existingPatient, request);

        patientRepository.save(updatedPatient);

        return patientMapper.createPatientResponse(existingPatient);
    }

    private Patient findPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PATIENT_NOT_FOUND));
    }

}
