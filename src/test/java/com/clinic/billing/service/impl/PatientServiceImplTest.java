package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.entity.enums.Gender;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    void testGetPatient_Success() {
        // Arrange
        Long patientId = 1L;
        Patient mockPatient = Patient.builder()
                .id(patientId)
                .name("John Doe")
                .age(30)
                .gender(Gender.MALE)
                .phone("1234567890")
                .address("123 Main St")
                .build();

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(mockPatient));

        // Act
        PatientResponse response = patientService.getPatient(patientId);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getPatientName());
        assertEquals("1234567890", response.getPatientPhone());
        assertEquals(30, response.getAge());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void testGetPatient_NotFound() {
        // Arrange
        Long patientId = 99L;
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            patientService.getPatient(patientId);
        });

        assertEquals(com.clinic.billing.utils.Constants.PATIENT_NOT_FOUND, exception.getMessage());
        verify(patientRepository, times(1)).findById(patientId);
    }

    @Test
    void testCreatePatient_Success() {
        // Arrange
        CreatePatientRequest request = new CreatePatientRequest();
        request.setName("Jane Doe");
        request.setAge(25);
        request.setGender("FEMALE");
        request.setPhone("0987654321");
        request.setAddress("456 Elm St");

        Patient savedPatient = Patient.builder()
                .id(2L)
                .name(request.getName())
                .age(request.getAge())
                .gender(Gender.FEMALE)
                .phone(request.getPhone())
                .address(request.getAddress())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        when(patientRepository.save(any(Patient.class))).thenReturn(savedPatient);

        // Act
        PatientResponse response = patientService.createPatient(request);

        // Assert
        assertNotNull(response);
        assertEquals("Jane Doe", response.getPatientName());
        assertEquals(2L, response.getPatientId());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }
}
