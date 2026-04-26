package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.SpecializationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecializationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private SpecializationServiceImpl specializationService;

    @Test
    void testCreateSpecialization_Success() {
        CreateSpecializationRequest request = new CreateSpecializationRequest("Cardiology", "ACTIVE");
        
        Specialization mockSpecialization = Specialization.builder()
                .id(1L)
                .name("Cardiology")
                .status(Status.ACTIVE)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        when(specializationRepository.save(any(Specialization.class))).thenReturn(mockSpecialization);

        SpecializationResponse response = specializationService.createSpecialization(request);

        assertNotNull(response);
        assertEquals(1L, response.getSpecializationId());
        assertEquals("Cardiology", response.getSpecializationName());
        verify(specializationRepository, times(1)).save(any(Specialization.class));
    }

    @Test
    void testGetAllSpecialization_Success() {
        Specialization spec1 = Specialization.builder().id(1L).name("Cardiology").status(Status.ACTIVE).build();
        Specialization spec2 = Specialization.builder().id(2L).name("Neurology").status(Status.ACTIVE).build();

        when(specializationRepository.findAll()).thenReturn(Arrays.asList(spec1, spec2));

        List<SpecializationResponse> responses = specializationService.getAllSpecialization();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Cardiology", responses.get(0).getSpecializationName());
        assertEquals("Neurology", responses.get(1).getSpecializationName());
        verify(specializationRepository, times(1)).findAll();
    }

    @Test
    void testGetBySpecializationId_Success() {
        Long id = 1L;
        Specialization spec = Specialization.builder().id(id).name("Cardiology").status(Status.ACTIVE).build();

        when(specializationRepository.findById(id)).thenReturn(Optional.of(spec));

        SpecializationResponse response = specializationService.getBySpecializationId(id);

        assertNotNull(response);
        assertEquals("Cardiology", response.getSpecializationName());
        verify(specializationRepository, times(1)).findById(id);
    }

    @Test
    void testGetBySpecializationId_NotFound() {
        Long id = 99L;
        when(specializationRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            specializationService.getBySpecializationId(id);
        });

        assertEquals("Specialization not found", exception.getMessage());
        verify(specializationRepository, times(1)).findById(id);
    }

    @Test
    void testUpdateSpecialization_Success() {
        Long id = 1L;
        CreateSpecializationRequest request = new CreateSpecializationRequest("Updated Cardiology", "INACTIVE");
        
        Specialization existingSpec = Specialization.builder().id(id).name("Cardiology").status(Status.ACTIVE).build();
        
        Specialization savedSpec = Specialization.builder().id(id).name("Updated Cardiology").status(Status.INACTIVE).build();

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existingSpec));
        when(specializationRepository.save(any(Specialization.class))).thenReturn(savedSpec);

        SpecializationResponse response = specializationService.updateSpecialization(id, request);

        assertNotNull(response);
        assertEquals("Updated Cardiology", response.getSpecializationName());
        verify(specializationRepository, times(1)).findById(id);
        verify(specializationRepository, times(1)).save(any(Specialization.class));
    }

    @Test
    void testDeleteSpecialization_Success() {
        Long id = 1L;
        Specialization existingSpec = Specialization.builder().id(id).name("Cardiology").status(Status.ACTIVE).build();

        when(specializationRepository.findById(id)).thenReturn(Optional.of(existingSpec));
        when(specializationRepository.save(any(Specialization.class))).thenReturn(existingSpec);

        specializationService.deleteSpecialization(id);

        assertEquals(Status.INACTIVE, existingSpec.getStatus());
        verify(specializationRepository, times(1)).findById(id);
        verify(specializationRepository, times(1)).save(existingSpec);
    }
}
