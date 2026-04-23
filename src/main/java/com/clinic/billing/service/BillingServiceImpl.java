package com.clinic.billing.service;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillItemResponse;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.*;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Transactional
    @Override
    public BillResponse createBill(CreateBillRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Specialization specialization = specializationRepository
                .findById(request.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        // 🔥 VALIDATION
        if (!doctor.getSpecialization().getId().equals(specialization.getId())) {
            throw new RuntimeException("Doctor does not belong to specialization");
        }

        List<BillItem> billItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (BillItemRequest item : request.getItems()) {

            MedicalService service = medicalServiceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            // 🔥 VALIDATION
            if (!service.getSpecialization().getId().equals(specialization.getId())) {
                throw new RuntimeException("Service does not belong to specialization");
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
                            .build()
            );
        }

        // 🔥 DISCOUNT LOGIC
        BigDecimal discountAmount = request.getDiscountAmount() != null
                ? request.getDiscountAmount() : BigDecimal.ZERO;

        BigDecimal discountPercent = request.getDiscountPercent() != null
                ? request.getDiscountPercent() : BigDecimal.ZERO;

        BigDecimal discountValue = discountPercent.compareTo(BigDecimal.ZERO) > 0
                ? subtotal.multiply(discountPercent).divide(BigDecimal.valueOf(100))
                : discountAmount;

        if (discountValue.compareTo(subtotal) > 0) {
            discountValue = subtotal;
        }

        BigDecimal total = subtotal.subtract(discountValue);

        Bill bill = Bill.builder()
                .billNumber(generateBillNumber())
                .patient(patient)
                .doctor(doctor)
                .specialization(specialization)
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
        Bill bill = billRepository.findById(id).orElseThrow(() -> new RuntimeException("Bill not found"));
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
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new RuntimeException("Bill already cancelled");
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

    // Temporary bill number generator (we’ll improve later)
    private String generateBillNumber() {
        return "INV-" + System.currentTimeMillis();
    }

    private BillResponse mapToResponse(Bill bill) {

        List<BillItemResponse> itemResponses = bill.getItems().stream()
                .map(item -> BillItemResponse.builder()
                        .serviceName(item.getServiceName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .gstPercentage(item.getGstPercentage())
                        .gstAmount(item.getGstAmount())
                        .lineTotal(item.getLineTotal())
                        .build()
                )
                .toList();

        return BillResponse.builder()
                .id(bill.getId())
                .billNumber(bill.getBillNumber())
                .patientName(bill.getPatient().getName())
                .patientPhone(bill.getPatient().getPhone())
                .doctorName(bill.getDoctorName())
                .subtotal(bill.getSubtotal())
                .totalGst(bill.getTotalGst())
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
