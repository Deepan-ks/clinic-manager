package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreatePatientRequest;
import com.clinic.billing.dto.request.UpdatePatientRequest;
import com.clinic.billing.dto.response.PatientResponse;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreatePatient_Success() throws Exception {
        CreatePatientRequest request = new CreatePatientRequest("Jane Doe", "1234567890", 25, "FEMALE", "jane@example.com", "123 St");
        
        PatientResponse response = PatientResponse.builder()
                .patientId(1L)
                .patientName("Jane Doe")
                .patientPhone("1234567890")
                .build();

        when(patientService.createPatient(any(CreatePatientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(1L))
                .andExpect(jsonPath("$.patientName").value("Jane Doe"));
    }

    @Test
    void testCreatePatient_ValidationError() throws Exception {
        // Name is blank, which should trigger @NotBlank validation
        CreatePatientRequest request = new CreatePatientRequest("", "1234567890", 25, "FEMALE", "jane@example.com", "123 St");

        mockMvc.perform(post("/api/v1/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").exists());
    }

    @Test
    void testGetPatient_Success() throws Exception {
        PatientResponse response = PatientResponse.builder()
                .patientId(1L)
                .patientName("Jane Doe")
                .build();

        when(patientService.getPatient(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientName").value("Jane Doe"));
    }

    @Test
    void testGetPatient_NotFound() throws Exception {
        when(patientService.getPatient(99L)).thenThrow(new ResourceNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/v1/patients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Patient not found"));
    }

    @Test
    void testGetAllPatients_Success() throws Exception {
        PatientResponse response1 = PatientResponse.builder().patientId(1L).patientName("Jane Doe").build();
        PatientResponse response2 = PatientResponse.builder().patientId(2L).patientName("John Smith").build();

        when(patientService.getAllPatients()).thenReturn(Arrays.asList(response1, response2));

        mockMvc.perform(get("/api/v1/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].patientName").value("Jane Doe"))
                .andExpect(jsonPath("$[1].patientName").value("John Smith"));
    }

    @Test
    void testUpdatePatient_Success() throws Exception {
        UpdatePatientRequest request = new UpdatePatientRequest("0987654321", "new@example.com", "456 St");
        
        PatientResponse response = PatientResponse.builder()
                .patientId(1L)
                .patientName("Jane Doe")
                .patientPhone("0987654321")
                .build();

        when(patientService.updatePatient(eq(1L), any(UpdatePatientRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientPhone").value("0987654321"));
    }

}
