package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.entity.Doctor;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.DoctorRepository;
import com.clinic.billing.repository.SpecializationRepository;
import com.clinic.billing.utils.mapper.DoctorMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorServiceImpl Test Suite")
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    // Test data builders
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2026, 5, 16, 15, 15, 19);
    private static final Long DOCTOR_ID = 10L;
    private static final Long SPEC_ID = 1L;
    private static final Long INVALID_SPEC_ID = 99L;
    private static final String DOCTOR_NAME = "Dr. Smith";
    private static final String DOCTOR_PHONE = "1234567890";
    private static final String SPEC_NAME = "Cardiology";

    private Specialization testSpecialization;
    private Doctor testDoctor;
    private DoctorResponse testDoctorResponse;
    private CreateDoctorRequest createDoctorRequest;

    @BeforeEach
    void setUp() {
        // Initialize reusable test data
        testSpecialization = buildSpecialization(SPEC_ID, SPEC_NAME);
        testDoctor = buildDoctor(DOCTOR_ID, DOCTOR_NAME, DOCTOR_PHONE, testSpecialization);
        testDoctorResponse = buildDoctorResponse(DOCTOR_NAME, SPEC_NAME);
        createDoctorRequest = new CreateDoctorRequest(DOCTOR_NAME, SPEC_ID, DOCTOR_PHONE, "ACTIVE");
    }

    @Nested
    @DisplayName("Create Doctor Tests")
    class CreateDoctorTests {

        @Test
        @DisplayName("Should successfully create a doctor")
        void testCreateDoctor_Success() {
            // Arrange
            when(specializationRepository.findById(SPEC_ID))
                    .thenReturn(Optional.of(testSpecialization));
            when(doctorMapper.createDoctorEntity(createDoctorRequest, testSpecialization))
                    .thenReturn(testDoctor);
            when(doctorRepository.save(testDoctor))
                    .thenReturn(testDoctor);
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(testDoctorResponse);

            // Act
            DoctorResponse response = doctorService.createDoctor(createDoctorRequest);

            // Assert
            assertNotNull(response);
            assertEquals(DOCTOR_NAME, response.getDoctorName());
            assertEquals(SPEC_NAME, response.getSpecializationName());

            // Verify interactions
            verify(specializationRepository).findById(SPEC_ID);
            verify(doctorMapper).createDoctorEntity(createDoctorRequest, testSpecialization);
            verify(doctorRepository).save(testDoctor);
            verify(doctorMapper).createDoctorResponse(testDoctor);
        }

        @Test
        @DisplayName("Should throw exception when specialization not found during creation")
        void testCreateDoctor_SpecializationNotFound() {
            // Arrange
            when(specializationRepository.findById(INVALID_SPEC_ID))
                    .thenReturn(Optional.empty());

            CreateDoctorRequest invalidRequest = new CreateDoctorRequest(
                    DOCTOR_NAME, INVALID_SPEC_ID, DOCTOR_PHONE, "ACTIVE"
            );

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.createDoctor(invalidRequest),
                    "Should throw ResourceNotFoundException when specialization not found"
            );

            // Verify no save occurred
            verify(doctorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Doctor Tests")
    class GetDoctorTests {

        @Test
        @DisplayName("Should successfully retrieve doctor by ID")
        void testGetDoctorById_Success() {
            // Arrange
            when(doctorRepository.findById(DOCTOR_ID))
                    .thenReturn(Optional.of(testDoctor));
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(testDoctorResponse);

            // Act
            DoctorResponse response = doctorService.getByDoctorId(DOCTOR_ID);

            // Assert
            assertNotNull(response);
            assertEquals(DOCTOR_NAME, response.getDoctorName());

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(doctorMapper).createDoctorResponse(testDoctor);
        }

        @Test
        @DisplayName("Should throw exception when doctor not found")
        void testGetDoctorById_NotFound() {
            // Arrange
            long nonExistentId = 999L;
            when(doctorRepository.findById(nonExistentId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.getByDoctorId(nonExistentId),
                    "Should throw ResourceNotFoundException when doctor not found"
            );

            // Verify
            verify(doctorRepository).findById(nonExistentId);
            verify(doctorMapper, never()).createDoctorResponse(any());
        }
    }

    @Nested
    @DisplayName("Get All Doctors by Specialization Tests")
    class GetAllDoctorsBySpecializationTests {

        @Test
        @DisplayName("Should retrieve all active doctors by specialization ID")
        void testGetAllDoctorBySpecialization_WithSpecId() {
            // Arrange
            Doctor inactiveDoctor = buildDoctor(11L, "Dr. Inactive", DOCTOR_PHONE, testSpecialization);
            inactiveDoctor.setStatus(Status.INACTIVE);

            List<Doctor> allDoctors = Arrays.asList(testDoctor, inactiveDoctor);
            when(doctorRepository.findBySpecializationId(SPEC_ID))
                    .thenReturn(allDoctors);
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(testDoctorResponse);

            // Act
            List<DoctorResponse> responses = doctorService.getAllDoctorBySpecialization(SPEC_ID);

            // Assert
            assertNotNull(responses);
            assertEquals(1, responses.size(), "Should only return active doctors");
            assertEquals(DOCTOR_NAME, responses.get(0).getDoctorName());

            // Verify
            verify(doctorRepository).findBySpecializationId(SPEC_ID);
            verify(doctorMapper, times(1)).createDoctorResponse(any());
        }

        @Test
        @DisplayName("Should retrieve all active doctors when specialization ID is null")
        void testGetAllDoctorBySpecialization_WithoutSpecId() {
            // Arrange
            List<Doctor> allDoctors = Arrays.asList(testDoctor);
            when(doctorRepository.findAll())
                    .thenReturn(allDoctors);
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(testDoctorResponse);

            // Act
            List<DoctorResponse> responses = doctorService.getAllDoctorBySpecialization(null);

            // Assert
            assertNotNull(responses);
            assertEquals(1, responses.size());

            // Verify
            verify(doctorRepository).findAll();
            verify(doctorRepository, never()).findBySpecializationId(anyLong());
        }

        @Test
        @DisplayName("Should return empty list when no active doctors found")
        void testGetAllDoctorBySpecialization_EmptyResult() {
            // Arrange
            when(doctorRepository.findBySpecializationId(SPEC_ID))
                    .thenReturn(Collections.emptyList());

            // Act
            List<DoctorResponse> responses = doctorService.getAllDoctorBySpecialization(SPEC_ID);

            // Assert
            assertNotNull(responses);
            assertTrue(responses.isEmpty(), "Should return empty list when no doctors found");

            // Verify
            verify(doctorRepository).findBySpecializationId(SPEC_ID);
            verify(doctorMapper, never()).createDoctorResponse(any());
        }
    }

    @Nested
    @DisplayName("Update Doctor Tests")
    class UpdateDoctorTests {

        @Test
        @DisplayName("Should successfully update doctor")
        void testUpdateDoctor_Success() {
            // Arrange
            CreateDoctorRequest updateRequest = new CreateDoctorRequest(
                    "Dr. John Smith", SPEC_ID, "0987654321", "ACTIVE"
            );
            DoctorResponse updatedResponse = buildDoctorResponse("Dr. John Smith", SPEC_NAME);

            when(doctorRepository.findById(DOCTOR_ID))
                    .thenReturn(Optional.of(testDoctor));
            when(specializationRepository.findById(SPEC_ID))
                    .thenReturn(Optional.of(testSpecialization));
            when(doctorRepository.save(any(Doctor.class)))
                    .thenReturn(testDoctor);
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(updatedResponse);

            // Act
            DoctorResponse response = doctorService.updateDoctor(DOCTOR_ID, updateRequest);

            // Assert
            assertNotNull(response);
            assertEquals("Dr. John Smith", response.getDoctorName());

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(specializationRepository).findById(SPEC_ID);
            verify(doctorRepository).save(any(Doctor.class));
            verify(doctorMapper).createDoctorResponse(any(Doctor.class));
        }

        @Test
        @DisplayName("Should throw exception when doctor not found during update")
        void testUpdateDoctor_DoctorNotFound() {
            // Arrange
            when(doctorRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.updateDoctor(DOCTOR_ID, createDoctorRequest)
            );

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(doctorRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when specialization not found during update")
        void testUpdateDoctor_SpecializationNotFound() {
            // Arrange
            when(doctorRepository.findById(DOCTOR_ID))
                    .thenReturn(Optional.of(testDoctor));
            when(specializationRepository.findById(INVALID_SPEC_ID))
                    .thenReturn(Optional.empty());

            CreateDoctorRequest invalidRequest = new CreateDoctorRequest(
                    DOCTOR_NAME, INVALID_SPEC_ID, DOCTOR_PHONE, "ACTIVE"
            );

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.updateDoctor(DOCTOR_ID, invalidRequest)
            );

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(specializationRepository).findById(INVALID_SPEC_ID);
            verify(doctorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Doctor Tests")
    class DeleteDoctorTests {

        @Test
        @DisplayName("Should successfully soft delete doctor")
        void testDeleteDoctor_Success() {
            // Arrange
            when(doctorRepository.findById(DOCTOR_ID))
                    .thenReturn(Optional.of(testDoctor));
            when(doctorRepository.save(any(Doctor.class)))
                    .thenReturn(testDoctor);

            // Act
            doctorService.deleteDoctor(DOCTOR_ID);

            // Assert
            assertEquals(Status.INACTIVE, testDoctor.getStatus());

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(doctorRepository).save(testDoctor);
        }

        @Test
        @DisplayName("Should throw exception when doctor not found during delete")
        void testDeleteDoctor_NotFound() {
            // Arrange
            when(doctorRepository.findById(anyLong()))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                    () -> doctorService.deleteDoctor(DOCTOR_ID)
            );

            // Verify
            verify(doctorRepository).findById(DOCTOR_ID);
            verify(doctorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Find All Doctors Tests")
    class FindAllDoctorsTests {

        @Test
        @DisplayName("Should retrieve all doctors")
        void testFindAllDoctors_Success() {
            // Arrange
            List<Doctor> allDoctors = Arrays.asList(testDoctor);
            when(doctorRepository.findAll())
                    .thenReturn(allDoctors);
            when(doctorMapper.createDoctorResponse(testDoctor))
                    .thenReturn(testDoctorResponse);

            // Act
            List<DoctorResponse> responses = doctorService.findAllDoctors();

            // Assert
            assertNotNull(responses);
            assertEquals(1, responses.size());
            assertEquals(DOCTOR_NAME, responses.get(0).getDoctorName());

            // Verify
            verify(doctorRepository).findAll();
            verify(doctorMapper).createDoctorResponse(testDoctor);
        }

        @Test
        @DisplayName("Should return empty list when no doctors exist")
        void testFindAllDoctors_Empty() {
            // Arrange
            when(doctorRepository.findAll())
                    .thenReturn(Collections.emptyList());

            // Act
            List<DoctorResponse> responses = doctorService.findAllDoctors();

            // Assert
            assertNotNull(responses);
            assertTrue(responses.isEmpty());

            // Verify
            verify(doctorRepository).findAll();
            verify(doctorMapper, never()).createDoctorResponse(any());
        }
    }

    // ==================== Test Data Builders ====================

    private Specialization buildSpecialization(Long id, String name) {
        return Specialization.builder()
                .id(id)
                .name(name)
                .status(Status.ACTIVE)
                .build();
    }

    private Doctor buildDoctor(Long id, String name, String phone, Specialization specialization) {
        return Doctor.builder()
                .id(id)
                .name(name)
                .phone(phone)
                .status(Status.ACTIVE)
                .specialization(specialization)
                .createdTime(FIXED_TIME)
                .updatedTime(FIXED_TIME)
                .build();
    }

    private DoctorResponse buildDoctorResponse(String doctorName, String specializationName) {
        return DoctorResponse.builder()
                .doctorName(doctorName)
                .specializationName(specializationName)
                .build();
    }
}