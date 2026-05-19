package com.clinic.billing.service.impl;

import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.BillRepository;
import com.clinic.billing.service.PdfService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private PdfService pdfService;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Test
    void testGenerateInvoice_BillNotFound() {
        when(billRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> invoiceService.generateInvoice(99L));
    }

    @Test
    void testGenerateInvoice_BillCancelled() {
        Bill bill = Bill.builder().id(1L).status(BillStatus.CANCELLED).build();
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        assertThrows(IllegalArgumentException.class, () -> invoiceService.generateInvoice(1L));
    }

    @Test
    void testGenerateInvoice_Success() throws Exception {
        Patient patient = Patient.builder().name("John Doe").address("123 St").age(30).build();
        Bill bill = Bill.builder()
                .id(1L)
                .billNumber("B-001")
                .patient(patient)
                .status(BillStatus.ACTIVE)
                .grandTotal(new BigDecimal("1000"))
                .createdTime(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(pdfService.buildPdf(bill)).thenReturn(new ByteArrayOutputStream());

        byte[] pdfBytes = invoiceService.generateInvoice(1L);
        
        assertNotNull(pdfBytes);
    }
}
