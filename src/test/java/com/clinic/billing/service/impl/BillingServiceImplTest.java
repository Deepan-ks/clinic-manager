package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.*;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceImplTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private MedicalServiceRepository medicalServiceRepository;

    @InjectMocks
    private BillingServiceImpl billingService;

    private Patient mockPatient;
    private Doctor mockDoctor;
    private Specialization mockSpecialization;
    private MedicalService mockService;
    private CreateBillRequest createBillRequest;

    @BeforeEach
    void setUp() {
        mockSpecialization = Specialization.builder().id(1L).name("Cardiology").status(Status.ACTIVE).build();

        mockPatient = Patient.builder().id(1L).name("John Doe").build();

        mockDoctor = Doctor.builder()
                .id(1L)
                .name("Dr. Smith")
                .specialization(mockSpecialization)
                .build();

        mockService = MedicalService.builder()
                .id(1L)
                .name("ECG")
                .price(new BigDecimal("500.00"))
                .specialization(mockSpecialization)
                .status(Status.ACTIVE)
                .build();

        BillItemRequest itemRequest = new BillItemRequest(1L, 2);
        createBillRequest = new CreateBillRequest();
        createBillRequest.setPatientId(1L);
        createBillRequest.setDoctorId(1L);
        createBillRequest.setSpecializationId(1L);
        createBillRequest.setItems(Arrays.asList(itemRequest));
        createBillRequest.setPaymentMode("CASH");
    }

    @Test
    void testCreateBill_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(mockSpecialization));
        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(mockService));

        Bill savedBill = Bill.builder()
                .id(100L)
                .billNumber("BILL-100")
                .patient(mockPatient)
                .doctor(mockDoctor)
                .specialization(mockSpecialization)
                .paymentMode(PaymentMode.CASH)
                .status(BillStatus.ACTIVE)
                .subtotal(new BigDecimal("1000.00"))
                .discountAmount(BigDecimal.ZERO)
                .grandTotal(new BigDecimal("1000.00"))
                .createdTime(LocalDateTime.now())
                .items(Arrays.asList(BillItem.builder()
                        .service(mockService)
                        .quantity(2)
                        .unitPrice(new BigDecimal("500.00"))
                        .lineTotal(new BigDecimal("1000.00"))
                        .build()))
                .build();

        when(billRepository.save(any(Bill.class))).thenReturn(savedBill);

        BillResponse response = billingService.createBill(createBillRequest);

        assertNotNull(response);
        assertEquals("BILL-100", response.getBillNumber());
        assertEquals(new BigDecimal("1000.00"), response.getGrandTotal());
        verify(billRepository, times(1)).save(any(Bill.class));
    }

    @Test
    void testCreateBill_DoctorMismatch() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(mockDoctor));
        
        Specialization differentSpec = Specialization.builder().id(2L).name("Neurology").build();
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(differentSpec));

        assertThrows(ResourceNotFoundException.class, () -> billingService.createBill(createBillRequest));
    }

    @Test
    void testGetBillById_Success() {
        Bill bill = Bill.builder()
                .id(1L)
                .billNumber("BILL-001")
                .patient(mockPatient)
                .doctor(mockDoctor)
                .specialization(mockSpecialization)
                .paymentMode(PaymentMode.CASH)
                .status(BillStatus.ACTIVE)
                .items(Arrays.asList())
                .build();

        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        BillResponse response = billingService.getBillById(1L);
        assertNotNull(response);
        assertEquals("BILL-001", response.getBillNumber());
    }

    @Test
    void testCancelBill_Success() {
        Bill bill = Bill.builder().id(1L).status(BillStatus.ACTIVE).build();
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));
        when(billRepository.save(any(Bill.class))).thenReturn(bill);

        CancelBillRequest cancelReq = new CancelBillRequest("Patient requested");
        billingService.cancelBill(1L, cancelReq);

        assertEquals(BillStatus.CANCELLED, bill.getStatus());
        assertTrue(bill.getNotes().contains("CANCELLED: Patient requested"));
        verify(billRepository, times(1)).save(bill);
    }

    @Test
    void testCancelBill_AlreadyCancelled() {
        Bill bill = Bill.builder().id(1L).status(BillStatus.CANCELLED).build();
        when(billRepository.findById(1L)).thenReturn(Optional.of(bill));

        assertThrows(ResourceNotFoundException.class, () -> billingService.cancelBill(1L, new CancelBillRequest("Test")));
    }
}
