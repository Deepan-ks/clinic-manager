package com.clinic.billing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BillItemResponse {

    private String serviceName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal gstPercentage;
    private BigDecimal gstAmount;
    private BigDecimal lineTotal;
}
