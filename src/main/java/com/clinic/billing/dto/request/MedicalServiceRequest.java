package com.clinic.billing.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalServiceRequest {
    private String name;
    private BigDecimal price;
    private BigDecimal gstPercentage;
    private Boolean isActive;

}
