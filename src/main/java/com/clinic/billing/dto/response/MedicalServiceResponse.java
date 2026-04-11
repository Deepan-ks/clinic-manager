package com.clinic.billing.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MedicalServiceResponse {
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
    private BigDecimal gstPercentage;
}
