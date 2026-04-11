package com.clinic.billing.service;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillItemResponse;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.BillItem;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Patient;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.repository.BillRepository;
import com.clinic.billing.repository.MedicalServiceRepository;
import com.clinic.billing.repository.PatientRepository;
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

    @Transactional
    @Override
    public BillResponse createBill(CreateBillRequest request) {
        // 🔹 1. Fetch patient
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<BillItem> billItems = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for(BillItemRequest item : request.getItems()) {
            MedicalService medicalService = medicalServiceRepository.findById(item.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));

            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            BigDecimal unitPrice = medicalService.getPrice();
            BigDecimal gstPercentage = medicalService.getGstPercentage();

            // GST calculation
            BigDecimal gstAmount = unitPrice
                    .multiply(quantity)
                    .multiply(gstPercentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal lineTotal = unitPrice
                    .multiply(quantity)
                    .add(gstAmount);

            subtotal = subtotal.add(unitPrice.multiply(quantity));
            totalGst = totalGst.add(gstAmount);

            // Create BillItem
            BillItem billItem = BillItem.builder()
                    .service(medicalService)
                    .serviceName(medicalService.getName()) // snapshot
                    .quantity(item.getQuantity())
                    .unitPrice(unitPrice)
                    .gstPercentage(gstPercentage)
                    .gstAmount(gstAmount)
                    .lineTotal(lineTotal)
                    .build();

            billItems.add(billItem);
        }

        BigDecimal grandTotal = subtotal.add(totalGst);

        // 🔹 3. Create Bill
        Bill bill = Bill.builder()
                .billNumber(generateBillNumber())
                .patient(patient)
                .doctorName(request.getDoctorName())
                .subtotal(subtotal)
                .totalGst(totalGst)
                .grandTotal(grandTotal)
                .paymentMode(PaymentMode.valueOf(request.getPaymentMode()))
                .status(BillStatus.ACTIVE)
                .notes(request.getNotes())
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .items(billItems)
                .build();

        // 🔹 4. Link items to bill
        billItems.forEach(item -> item.setBill(bill));

        // 🔹 5. Save everything
        Bill savedBill = billRepository.save(bill);
        return mapToResponse(savedBill);
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
                .paymentMode(bill.getPaymentMode().name())
                .status(bill.getStatus().name())
                .items(itemResponses)
                .createdTime(bill.getCreatedTime())
                .build();
    }
}
