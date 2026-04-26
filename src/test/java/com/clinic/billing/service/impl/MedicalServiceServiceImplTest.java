package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.MedicalServiceRepository;
import com.clinic.billing.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalServiceServiceImplTest {

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private MedicalServiceServiceImpl medicalServiceService;

    private Specialization createMockSpecialization() {
        return Specialization.builder()
                .id(1L)
                .name("Cardiology")
                .status(Status.ACTIVE)
                .build();
    }

    private MedicalService createMockMedicalService() {
        return MedicalService.builder()
                .id(100L)
                .name("ECG")
                .description("Electrocardiogram")
                .price(new BigDecimal("500.00"))
                .status(Status.ACTIVE)
                .specialization(createMockSpecialization())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateMedicalService_Success() {
        MedicalServiceRequest request = new MedicalServiceRequest("ECG", new BigDecimal("500.00"), Status.ACTIVE, 1L, "Electrocardiogram");
        
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(createMockSpecialization()));
        when(medicalServiceRepository.save(any(MedicalService.class))).thenReturn(createMockMedicalService());

        MedicalServiceResponse response = medicalServiceService.createMedicalService(request);

        assertNotNull(response);
        assertEquals("ECG", response.getServiceName());
        verify(specializationRepository, times(1)).findById(1L);
        verify(medicalServiceRepository, times(1)).save(any(MedicalService.class));
    }

    @Test
    void testCreateMedicalService_SpecializationNotFound() {
        MedicalServiceRequest request = new MedicalServiceRequest("ECG", new BigDecimal("500.00"), Status.ACTIVE, 99L, "Electrocardiogram");
        
        when(specializationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicalServiceService.createMedicalService(request));
    }

    @Test
    void testGetAllActiveServices_Success() {
        when(medicalServiceRepository.findByStatus(Status.ACTIVE)).thenReturn(Arrays.asList(createMockMedicalService()));

        List<MedicalServiceResponse> responses = medicalServiceService.getAllActiveServices();

        assertEquals(1, responses.size());
        assertEquals("ECG", responses.get(0).getServiceName());
    }

    @Test
    void testFindBySpecializationById_Success() {
        when(medicalServiceRepository.findBySpecializationId(1L)).thenReturn(Arrays.asList(createMockMedicalService()));

        List<MedicalServiceResponse> responses = medicalServiceService.findBySpecializationById(1L);

        assertEquals(1, responses.size());
        assertEquals("ECG", responses.get(0).getServiceName());
    }

    @Test
    void testUpdateMedicalService_Success() {
        MedicalServiceRequest request = new MedicalServiceRequest("Updated ECG", new BigDecimal("600.00"), Status.INACTIVE, 1L, "Updated Desc");
        MedicalService existingService = createMockMedicalService();
        
        when(medicalServiceRepository.findById(100L)).thenReturn(Optional.of(existingService));
        when(medicalServiceRepository.save(any(MedicalService.class))).thenReturn(existingService);

        MedicalServiceResponse response = medicalServiceService.updateMedicalService(100L, request);

        assertNotNull(response);
        verify(medicalServiceRepository, times(1)).save(existingService);
    }

    @Test
    void testDeleteMedicalService_Success() {
        MedicalService existingService = createMockMedicalService();
        when(medicalServiceRepository.findById(100L)).thenReturn(Optional.of(existingService));
        when(medicalServiceRepository.save(any(MedicalService.class))).thenReturn(existingService);

        medicalServiceService.deleteMedicalService(100L);

        assertEquals(Status.INACTIVE, existingService.getStatus());
        verify(medicalServiceRepository, times(1)).save(existingService);
    }
}
