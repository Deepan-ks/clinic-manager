package com.clinic.billing.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequest {

    @NotNull(message = Constants.PATIENT_ID_REQUIRED)
    private Long patientId;

    @NotNull(message = Constants.DOCTOR_ID_REQUIRED)
    private Long doctorId;

    @NotNull(message = Constants.SPECIALIZATION_ID_REQUIRED)
    private Long specializationId;

    private String doctorName;

    @NotBlank(message = Constants.PAYMENT_MODE_REQUIRED)
    private String paymentMode;
    private String notes;

    private BigDecimal discountAmount;
    private BigDecimal discountPercent;

    @NotEmpty(message = Constants.BILL_ITEMS_REQUIRED)
    @Valid
    private List<BillItemRequest> items;
}
