package com.clinic.billing.service.impl;

import com.clinic.billing.dto.request.BillItemRequest;
import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import com.clinic.billing.entity.*;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.*;
import com.clinic.billing.service.BillSequenceService;
import com.clinic.billing.service.BillingService;
import com.clinic.billing.utils.Constants;
import com.clinic.billing.utils.mapper.BillingMapper;
import com.clinic.billing.utils.validator.BillingValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {

    private static final LocalDateTime DEFAULT_FROM_DATE = LocalDateTime.of(1900, 1, 1, 0, 0);
    private static final LocalDateTime DEFAULT_TO_DATE = LocalDateTime.of(2100, 12, 31, 23, 59, 59);

    private final BillRepository billRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final BillSequenceService billSequenceService;
    private final BillingValidator billingValidator;
    private final BillingMapper billingMapper;
    

    @Transactional
    @Override
    public BillResponse createBill(CreateBillRequest request) throws BadRequestException {

        Patient patient = findPatient(request.getPatientId());
        Doctor doctor = findDoctor(request.getDoctorId());
        Specialization specialization = findSpecialization(request.getSpecializationId());

        billingValidator.validateDoctorSpecialization(doctor, specialization);

        List<BillItem> billItems = createBillItems(request, specialization);

        BigDecimal subtotal = calculateSubtotal(billItems);
        BigDecimal discountPercent = getDiscountPercent(request);
        BigDecimal discountAmount = getDiscountAmount(request);
        billingValidator.validateDiscount(discountAmount, discountPercent, subtotal);

        BigDecimal finalDiscount = calculateFinalDiscount(subtotal, discountAmount, discountPercent);
        BigDecimal total = subtotal.subtract(finalDiscount);
        billingValidator.validateTotal(total);

        PaymentMode paymentMode = billingValidator.parsePaymentMode(request.getPaymentMode());

        Bill bill = billingMapper.createBillEntity(billSequenceService.generateBillNumber(), patient,
                doctor, specialization, request, subtotal, finalDiscount, discountPercent, total, paymentMode, billItems);

        billItems.forEach(item -> item.setBill(bill));

        Bill savedBill = billRepository.save(bill);

        return billingMapper.createBillResponse(savedBill);
    }

    @Override
    public BillResponse getBillById(Long id) {
        Bill bill = findBillById(id);
        return billingMapper.createBillResponse(bill);
    }

    @Override
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(billingMapper::createBillResponse)
                .toList();
    }

    @Override
    public Page<BillResponse> getBills(String search, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        String query = (search == null) ? "" : search.trim();

        // Use extreme dates to represent "all time" if no filter is provided
        LocalDateTime from = (fromDate != null) ? fromDate.atStartOfDay() : DEFAULT_FROM_DATE;
        LocalDateTime to   = (toDate   != null) ? toDate.atTime(23, 59, 59) : DEFAULT_TO_DATE;

        return billRepository.findAllPaged(query, from, to, pageable)
                .map(billingMapper::createBillResponse);
    }

    @Override
    public void cancelBill(Long billId, CancelBillRequest cancelBillRequest) {
        Bill bill = findBillById(billId);
        billingValidator.validateBillCancellation(bill);

        // Update status
        bill.setStatus(BillStatus.CANCELLED);

        // Append cancellation reason
        String existingNotes = bill.getNotes() != null ? bill.getNotes() : "";
        String cancelNote = "CANCELLED: " + cancelBillRequest.getReason();
        bill.setNotes(existingNotes + " | " + cancelNote);
        bill.setUpdatedTime(LocalDateTime.now());

        billRepository.save(bill);
    }


    /* ================= BILL HELPERS ================= */

    private List<BillItem> createBillItems(CreateBillRequest request, Specialization specialization) {

        List<BillItem> billItems = new ArrayList<>();

        for (BillItemRequest item : request.getItems()) {
            MedicalService service = findMedicalService(item);

            billingValidator.validateBillItemService(service, specialization);

            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal lineTotal = service.getPrice().multiply(quantity);

            // create bill item entity and add to list
            billItems.add(billingMapper.createBillItemEntity(service, item, lineTotal));
        }
        return billItems;
    }

    private BigDecimal calculateSubtotal(List<BillItem> billItems) {
        return billItems.stream()
                .map(BillItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateFinalDiscount(BigDecimal subtotal, BigDecimal discountAmount, BigDecimal discountPercent) {
        if (discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            return subtotal.multiply(discountPercent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return discountAmount;
    }

    private BigDecimal getDiscountAmount(CreateBillRequest request) {
        return request.getDiscountAmount() != null
                ? request.getDiscountAmount()
                : BigDecimal.ZERO;
    }

    private BigDecimal getDiscountPercent(CreateBillRequest request) {
        return request.getDiscountPercent() != null
                ? request.getDiscountPercent()
                : BigDecimal.ZERO;
    }
    
    /* ================== REPOSITORY CALLS ================== */

    private @NonNull Bill findBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.BILL_NOT_FOUND));
    }

    private @NonNull MedicalService findMedicalService(BillItemRequest item) {
        return medicalServiceRepository.findById(item.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SERVICE_NOT_FOUND));
    }

    private @NonNull Specialization findSpecialization(Long specializationId) {
        return specializationRepository
                .findById(specializationId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.SPECIALIZATION_NOT_FOUND));
    }

    private @NonNull Doctor findDoctor(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND));
    }

    private @NonNull Patient findPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.PATIENT_NOT_FOUND));
    }
}
