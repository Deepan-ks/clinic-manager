package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillItemResponse;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.*;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.repository.*;
import com.clinic.billing.service.BillSequenceService;
import com.clinic.billing.service.BillingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clinic.billing.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private final BillRepository billRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final BillSequenceService billSequenceService;

    private PaymentMode paymentMode;

    @Transactional
    @Override
    public BillResponse createBill(CreateBillRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        Specialization specialization = specializationRepository
                .findById(request.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException("Specialization not found"));

        // 🔥 VALIDATION
        if (!doctor.getSpecialization().getId().equals(specialization.getId())) {
            throw new IllegalArgumentException("Doctor does not belong to specialization");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one service is required");
        }

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillItemRequest item : request.getItems()) {

            MedicalService service = medicalServiceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

            // 🔥 VALIDATION
            if (!service.getSpecialization().getId().equals(specialization.getId())) {
                throw new IllegalArgumentException("Service does not belong to specialization");
            }

            if (service.getStatus().equals(Status.INACTIVE)) {
                throw new IllegalArgumentException("Service status is INACTIVE");
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be at least 1");
            }

            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal lineTotal = service.getPrice().multiply(qty);

            subtotal = subtotal.add(lineTotal);

            billItems.add(
                    BillItem.builder()
                            .service(service)
                            .serviceName(service.getName())
                            .quantity(item.getQuantity())
                            .unitPrice(service.getPrice())
                            .lineTotal(lineTotal)
                            .build());
        }

        // 🔥 DISCOUNT LOGIC
        BigDecimal discountAmount = request.getDiscountAmount() != null
                ? request.getDiscountAmount()
                : BigDecimal.ZERO;

        BigDecimal discountPercent = request.getDiscountPercent() != null
                ? request.getDiscountPercent()
                : BigDecimal.ZERO;

        if (discountAmount.compareTo(BigDecimal.ZERO) > 0 &&
                discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Use either discount amount or percent, not both");
        }

        BigDecimal discountValue = discountPercent.compareTo(BigDecimal.ZERO) > 0
                ? subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : discountAmount;

        if (discountValue.compareTo(subtotal) > 0) {
            discountValue = subtotal;
        }

        if (discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Discount cannot exceed 100%");
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Discount exceeds subtotal");
        }

        BigDecimal total = subtotal.subtract(discountValue);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total cannot be negative");
        }

        Bill bill = Bill.builder()
                .billNumber(getBillNumber())
                .patient(patient)
                .doctor(doctor)
                .specialization(specialization)
                .doctorName(request.getDoctorName())
                .subtotal(subtotal)
                .discountAmount(discountValue)
                .discountPercent(discountPercent)
                .grandTotal(total)
                .paymentMode(paymentMode)
                .status(BillStatus.ACTIVE)
                .items(billItems)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();

        billItems.forEach(i -> i.setBill(bill));

        return mapToResponse(billRepository.save(bill));
    }

    @Override
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bill not found"));
        return mapToResponse(bill);
    }

    @Override
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void cancelBill(Long billId, CancelBillRequest cancelBillRequest) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found"));

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new ResourceNotFoundException("Bill already cancelled");
        }

        // Update status
        bill.setStatus(BillStatus.CANCELLED);

        // Append cancellation reason
        String existingNotes = bill.getNotes() != null ? bill.getNotes() : "";
        String cancelNote = "CANCELLED: " + cancelBillRequest.getReason();

        bill.setNotes(existingNotes + " | " + cancelNote);

        bill.setUpdatedTime(LocalDateTime.now());

        billRepository.save(bill);
    }

    private String getBillNumber() {
        return billSequenceService.generateBillNumber();
    }

    private BillResponse mapToResponse(Bill bill) {

        List<BillItemResponse> itemResponses = bill.getItems().stream()
                .map(item -> BillItemResponse.builder()
                        .serviceName(item.getServiceName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        return BillResponse.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .patientName(bill.getPatient().getName())
                .patientPhone(bill.getPatient().getPhone())
                .patientAddress(bill.getPatient().getAddress())
                .doctorName(bill.getDoctorName())
                .subtotal(bill.getSubtotal())
                .grandTotal(bill.getGrandTotal())
                .discountAmount(bill.getDiscountAmount())
                .discountPercent(bill.getDiscountPercent())
                .paymentMode(bill.getPaymentMode().name())
                .status(bill.getStatus().name())
                .items(itemResponses)
                .createdTime(bill.getCreatedTime())
                .build();
    }
}
