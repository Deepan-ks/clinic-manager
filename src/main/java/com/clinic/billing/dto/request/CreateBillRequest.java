package com.clinic.billing.dto.request;

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

    private Long patientId;
    private Long doctorId;
    private Long specializationId;

    private String doctorName;

    private String paymentMode;
    private String notes;

    private BigDecimal discountAmount;
    private BigDecimal discountPercent;

    private List<BillItemRequest> items;
}
