package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.MedicalServiceRepository;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.service.MedicalServiceService;
import com.clinic.billing.utils.Constants;
import com.clinic.billing.utils.mapper.MedicalServiceMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalServiceServiceImpl implements MedicalServiceService {

    private final MedicalServiceRepository medicalServiceRepository;
    private final SpecializationRepository specializationRepository;
    private final MedicalServiceMapper medicalServiceMapper;

    @Override
    public Page<MedicalServiceResponse> getServices(String search, Long specializationId, Pageable pageable) {

        String query = normalizeSearch(search);

        return medicalServiceRepository.findAllPaged(query, specializationId, pageable)
                .map(medicalServiceMapper::createMedicalServiceResponse);
    }

    @Override
    public MedicalServiceResponse createMedicalService(MedicalServiceRequest request) {

        Specialization specialization = findSpecialization(request.getSpecializationId());
        MedicalService medicalService = medicalServiceMapper.createMedicalServiceEntity(request, specialization);
        MedicalService saved = medicalServiceRepository.save(medicalService);

        return medicalServiceMapper.createMedicalServiceResponse(saved);
    }

    @Override
    public MedicalServiceResponse updateMedicalService(Long id, MedicalServiceRequest request) {

        MedicalService existingService = findMedicalServiceById(id);
        MedicalService updateMedicalService = medicalServiceMapper.updateMedicalServiceEntity(existingService, request);
        MedicalService saved = medicalServiceRepository.save(updateMedicalService);

        return medicalServiceMapper.createMedicalServiceResponse(saved);
    }

    @Override
    public void deleteMedicalService(Long id) {

        MedicalService existingService = findMedicalServiceById(id);
        existingService.setStatus(Status.INACTIVE);
        medicalServiceRepository.save(existingService);
    }

    @Override
    public List<MedicalServiceResponse> findBySpecializationById(Long specializationId) {

        return medicalServiceRepository.findBySpecializationId(specializationId)
                .stream()
                .map(medicalServiceMapper::createMedicalServiceResponse)
                .toList();
    }

    @Override
    public List<MedicalServiceResponse> getAllActiveServices() {

        return medicalServiceRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(medicalServiceMapper::createMedicalServiceResponse)
                .toList();
    }

    private MedicalService findMedicalServiceById(Long id) {
        return medicalServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.MEDICAL_SERVICE_NOT_FOUND));
    }

    private Specialization findSpecialization(Long id) {
        return specializationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));
    }

    private String normalizeSearch(String search) {
        return search == null ? "" : search.trim();
    }
}