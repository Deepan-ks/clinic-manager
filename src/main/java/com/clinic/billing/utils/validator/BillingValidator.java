package com.clinic.billing.utils.validator;

import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.Doctor;
import com.clinic.billing.entity.MedicalService;
import com.clinic.billing.entity.Specialization;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.entity.enums.PaymentMode;
import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.utils.Constants;
import jakarta.validation.constraints.NotBlank;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class BillingValidator {

    public void validateDoctorSpecialization(Doctor doctor, Specialization specialization) {
        if (!doctor.getSpecialization().getId().equals(specialization.getId())) {
            throw new IllegalArgumentException(Constants.DOCTOR_SPECIALIZATION_MISMATCH);
        }
    }

    public void validateBillItemService(MedicalService service, Specialization specialization) {
        if (!service.getSpecialization().getId().equals(specialization.getId())) {
            throw new IllegalArgumentException(Constants.SERVICE_SPECIALIZATION_MISMATCH);
        }
        if (service.getStatus() == Status.INACTIVE) {
            throw new IllegalArgumentException(Constants.SERVICE_INACTIVE);
        }
    }

    public void validateDiscount(BigDecimal discountAmount, BigDecimal discountPercent, BigDecimal subtotal) {

        if (discountAmount.compareTo(BigDecimal.ZERO) > 0 && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Use either discount amount or percent, not both");
        }

        if (discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException(Constants.INVALID_DISCOUNT_PERCENT);
        }

        if (discountAmount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(Constants.INVALID_DISCOUNT_AMOUNT);
        }
    }

    public void validateTotal(BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(Constants.NEGATIVE_TOTAL);
        }
    }

    public void validateBillCancellation(Bill bill) {
        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new IllegalArgumentException(Constants.BILL_ALREADY_CANCELLED);
        }
    }

    public PaymentMode parsePaymentMode(String paymentMode) throws BadRequestException {
        try {
            return PaymentMode.valueOf(paymentMode.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BadRequestException(Constants.INVALID_PAYMENT_MODE + " Valid values: " + Arrays.toString(PaymentMode.values()));
        }
    }
}
