package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreateSpecializationRequest;
import com.clinic.billing.dto.response.SpecializationResponse;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.service.SpecializationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecializationController.class)
public class SpecializationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecializationService specializationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSpecialization_Success() throws Exception {
        CreateSpecializationRequest request = new CreateSpecializationRequest("Cardiology", "ACTIVE");
        SpecializationResponse response = SpecializationResponse.builder()
                .specializationId(1L)
                .specializationName("Cardiology")
                .build();

        when(specializationService.createSpecialization(any(CreateSpecializationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/specializations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specializationId").value(1))
                .andExpect(jsonPath("$.specializationName").value("Cardiology"));
    }

    @Test
    void testGetAllSpecialization_Success() throws Exception {
        SpecializationResponse resp1 = SpecializationResponse.builder().specializationId(1L).specializationName("Cardiology").build();
        SpecializationResponse resp2 = SpecializationResponse.builder().specializationId(2L).specializationName("Neurology").build();

        when(specializationService.getAllSpecialization()).thenReturn(Arrays.asList(resp1, resp2));

        mockMvc.perform(get("/api/v1/specializations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].specializationName").value("Cardiology"));
    }

    @Test
    void testGetBySpecializationId_Success() throws Exception {
        SpecializationResponse response = SpecializationResponse.builder().specializationId(1L).specializationName("Cardiology").build();

        when(specializationService.getBySpecializationId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/specializations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specializationName").value("Cardiology"));
    }

    @Test
    void testGetBySpecializationId_NotFound() throws Exception {
        when(specializationService.getBySpecializationId(99L)).thenThrow(new ResourceNotFoundException("Specialization not found"));

        mockMvc.perform(get("/api/v1/specializations/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Specialization not found"));
    }

    @Test
    void testUpdateSpecialization_Success() throws Exception {
        CreateSpecializationRequest request = new CreateSpecializationRequest("Cardiology Updated", "ACTIVE");
        SpecializationResponse response = SpecializationResponse.builder().specializationId(1L).specializationName("Cardiology Updated").build();

        when(specializationService.updateSpecialization(eq(1L), any(CreateSpecializationRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/specializations/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specializationName").value("Cardiology Updated"));
    }
}
