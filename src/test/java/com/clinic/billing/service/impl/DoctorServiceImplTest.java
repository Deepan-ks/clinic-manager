package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.entity.Doctor;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.DoctorRepository;
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
public class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Specialization createMockSpecialization() {
        return Specialization.builder().id(1L).name("Cardiology").status(Status.ACTIVE).build();
    }

    private Doctor createMockDoctor() {
        return Doctor.builder()
                .id(10L)
                .name("Dr. Smith")
                .phone("1234567890")
                .status(Status.ACTIVE)
                .specialization(createMockSpecialization())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateDoctor_Success() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Smith", 1L, "1234567890", "ACTIVE");
        
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(createMockSpecialization()));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(createMockDoctor());

        DoctorResponse response = doctorService.createDoctor(request);

        assertNotNull(response);
        assertEquals("Dr. Smith", response.getDoctorName());
        assertEquals("Cardiology", response.getSpecializationName());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    void testCreateDoctor_SpecializationNotFound() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. Smith", 99L, "1234567890", "ACTIVE");
        when(specializationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.createDoctor(request));
    }

    @Test
    void testGetDoctorById_Success() {
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(createMockDoctor()));

        DoctorResponse response = doctorService.getByDoctorId(10L);

        assertNotNull(response);
        assertEquals("Dr. Smith", response.getDoctorName());
    }

    @Test
    void testGetDoctorById_NotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getByDoctorId(99L));
    }

    @Test
    void testGetAllDoctorBySpecialization_WithSpecId() {
        when(doctorRepository.findBySpecializationId(1L)).thenReturn(Arrays.asList(createMockDoctor()));

        List<DoctorResponse> responses = doctorService.getAllDoctorBySpecialization(1L);

        assertEquals(1, responses.size());
        assertEquals("Dr. Smith", responses.get(0).getDoctorName());
    }

    @Test
    void testGetAllDoctorBySpecialization_WithoutSpecId() {
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(createMockDoctor()));

        List<DoctorResponse> responses = doctorService.getAllDoctorBySpecialization(null);

        assertEquals(1, responses.size());
    }

    @Test
    void testUpdateDoctor_Success() {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. John Smith", 1L, "0987654321", "INACTIVE");
        Doctor existingDoctor = createMockDoctor();

        when(doctorRepository.findById(10L)).thenReturn(Optional.of(existingDoctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(createMockSpecialization()));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(existingDoctor);

        DoctorResponse response = doctorService.updateDoctor(10L, request);

        assertNotNull(response);
        verify(doctorRepository, times(1)).save(existingDoctor);
    }

    @Test
    void testDeleteDoctor_Success() {
        Doctor existingDoctor = createMockDoctor();
        when(doctorRepository.findById(10L)).thenReturn(Optional.of(existingDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(existingDoctor);

        doctorService.deleteDoctor(10L);

        assertEquals(Status.INACTIVE, existingDoctor.getStatus());
        verify(doctorRepository, times(1)).save(existingDoctor);
    }
}
