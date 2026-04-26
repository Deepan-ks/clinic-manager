package com.clinic.billing.controller;

import com.clinic.billing.dto.request.MedicalServiceRequest;
import com.clinic.billing.dto.response.MedicalServiceResponse;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.service.MedicalServiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalServiceController.class)
public class MedicalServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalServiceService medicalServiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateService_Success() throws Exception {
        MedicalServiceRequest request = new MedicalServiceRequest("Consultation", new BigDecimal("500.00"), Status.ACTIVE, 1L, "General checkup");
        MedicalServiceResponse response = MedicalServiceResponse.builder().serviceId(1L).serviceName("Consultation").price(new BigDecimal("500.00")).build();

        when(medicalServiceService.createMedicalService(any(MedicalServiceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/services")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceName").value("Consultation"));
    }

    @Test
    void testGetAllActiveServices_Success() throws Exception {
        MedicalServiceResponse response = MedicalServiceResponse.builder().serviceId(1L).serviceName("Consultation").build();
        when(medicalServiceService.getAllActiveServices()).thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/v1/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateService_Success() throws Exception {
        MedicalServiceRequest request = new MedicalServiceRequest("Consultation Updated", new BigDecimal("600.00"), Status.ACTIVE, 1L, "General checkup");
        MedicalServiceResponse response = MedicalServiceResponse.builder().serviceId(1L).serviceName("Consultation Updated").build();

        when(medicalServiceService.updateMedicalService(eq(1L), any(MedicalServiceRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/services/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("Consultation Updated"));
    }
}
