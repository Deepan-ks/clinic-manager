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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    private String doctorName;

    @NotBlank(message = "Payment mode is required")
    private String paymentMode;
    private String notes;

    private BigDecimal discountAmount;
    private BigDecimal discountPercent;

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<BillItemRequest> items;
}
