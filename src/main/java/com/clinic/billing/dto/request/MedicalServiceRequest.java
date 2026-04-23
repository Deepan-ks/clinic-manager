package com.clinic.billing.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceRequest {

    private String name;
    private BigDecimal price;
    private BigDecimal gstPercentage;
    private Boolean isActive;
    private Long specializationId;
}