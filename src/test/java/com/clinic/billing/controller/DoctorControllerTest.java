package com.clinic.billing.controller;

import com.clinic.billing.dto.request.CreateDoctorRequest;
import com.clinic.billing.dto.response.DoctorResponse;
import com.clinic.billing.service.DoctorService;
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

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateDoctor_Success() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. John", 1L, "1234567890", "ACTIVE");
        DoctorResponse response = DoctorResponse.builder().doctorId(1L).doctorName("Dr. John").build();

        when(doctorService.createDoctor(any(CreateDoctorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. John"));
    }

    @Test
    void testGetAllDoctors_Success() throws Exception {
        DoctorResponse resp1 = DoctorResponse.builder().doctorId(1L).doctorName("Dr. John").build();
        when(doctorService.getAllDoctorBySpecialization(null)).thenReturn(Arrays.asList(resp1));

        mockMvc.perform(get("/api/v1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetByDoctorId_Success() throws Exception {
        DoctorResponse response = DoctorResponse.builder().doctorId(1L).doctorName("Dr. John").build();
        when(doctorService.getByDoctorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName").value("Dr. John"));
    }

    @Test
    void testUpdateDoctor_Success() throws Exception {
        CreateDoctorRequest request = new CreateDoctorRequest("Dr. John Updated", 1L, "1234567890", "ACTIVE");
        DoctorResponse response = DoctorResponse.builder().doctorId(1L).doctorName("Dr. John Updated").build();

        when(doctorService.updateDoctor(eq(1L), any(CreateDoctorRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorName").value("Dr. John Updated"));
    }
}
