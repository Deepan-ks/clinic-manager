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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.utils.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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

    @Transactional
    @Override
    public BillResponse createBill(CreateBillRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND));

        Specialization specialization = specializationRepository
                .findById(request.getSpecializationId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));

        // 🔥 VALIDATION
        if (!doctor.getSpecialization().getId().equals(specialization.getId())) {
            throw new IllegalArgumentException(Constants.DOCTOR_SPECIALIZATION_MISMATCH);
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException(Constants.BILL_ITEMS_REQUIRED);
        }

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillItemRequest item : request.getItems()) {

            MedicalService service = medicalServiceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.SERVICE_NOT_FOUND));

            // 🔥 VALIDATION
            if (!service.getSpecialization().getId().equals(specialization.getId())) {
                throw new IllegalArgumentException(Constants.SERVICE_SPECIALIZATION_MISMATCH);
            }

            if (service.getStatus().equals(Status.INACTIVE)) {
                throw new IllegalArgumentException(Constants.SERVICE_INACTIVE);
            }

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException(Constants.QUANTITY_MIN);
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

        // Validate limits BEFORE computing discountValue
        if (discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(Constants.INVALID_DISCOUNT_PERCENT);
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(Constants.INVALID_DISCOUNT_AMOUNT);
        }

        BigDecimal discountValue = discountPercent.compareTo(BigDecimal.ZERO) > 0
                ? subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                : discountAmount;

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
                .paymentMode(PaymentMode.valueOf(request.getPaymentMode()))
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
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.BILL_NOT_FOUND));
        return mapToResponse(bill);
    }

    @Override
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<BillResponse> getBills(String search, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        String q = (search == null) ? "" : search.trim();
        // Use extreme dates to represent "all time" if no filter is provided
        LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : LocalDateTime.of(1900, 1, 1, 0, 0);
        LocalDateTime to   = (toDate   != null) ? toDate.atTime(23, 59, 59) : LocalDateTime.of(2100, 12, 31, 23, 59);
        return billRepository.findAllPaged(q, from, to, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public void cancelBill(Long billId, CancelBillRequest cancelBillRequest) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.BILL_NOT_FOUND));

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new IllegalArgumentException(Constants.BILL_ALREADY_CANCELLED);
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
                .notes(bill.getNotes())
                .items(itemResponses)
                .createdTime(bill.getCreatedTime())
                .build();
    }
}
