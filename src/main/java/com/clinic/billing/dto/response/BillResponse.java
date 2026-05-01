package com.clinic.billing.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {

    private Long id;
    private String billNumber;

    private String patientName;
    private String patientPhone;
    private String patientAddress;

    private String doctorName;

    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal discountPercent;
    private BigDecimal grandTotal;

    private String paymentMode;
    private String status;

    private String notes;

    private List<BillItemResponse> items;

    private LocalDateTime createdTime;
}