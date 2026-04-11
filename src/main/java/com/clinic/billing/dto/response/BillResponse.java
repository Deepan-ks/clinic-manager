package com.clinic.billing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class BillResponse {

    private Long id;
    private String billNumber;

    private String patientName;
    private String patientPhone;

    private String doctorName;

    private BigDecimal subtotal;
    private BigDecimal totalGst;
    private BigDecimal grandTotal;

    private String paymentMode;
    private String status;

    private List<BillItemResponse> items;

    private LocalDateTime createdTime;
}