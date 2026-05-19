package com.clinic.billing.controller;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.service.BillingService;
import com.clinic.billing.service.InvoiceService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BillController.class)
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BillingService billingService;

    @MockitoBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateBill_Success() throws Exception {
        CreateBillRequest request = new CreateBillRequest();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setSpecializationId(1L);
        request.setDoctorName("Dr. Test");
        request.setPaymentMode("CASH");
        request.setItems(Arrays.asList(new BillItemRequest(1L, 1)));

        BillResponse response = BillResponse.builder().billNumber("BILL-001").grandTotal(new BigDecimal("500.00"))
                .build();

        when(billingService.createBill(any(CreateBillRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.billNumber").value("BILL-001"));
    }

    @Test
    void testGetBill_Success() throws Exception {
        BillResponse response = BillResponse.builder().billNumber("BILL-001").build();
        when(billingService.getBillById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/bills/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.billNumber").value("BILL-001"));
    }

    @Test
    void testCancelBill_Success() throws Exception {
        CancelBillRequest request = new CancelBillRequest("Patient request");
        doNothing().when(billingService).cancelBill(eq(1L), any(CancelBillRequest.class));

        mockMvc.perform(patch("/api/v1/bills/1/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}
