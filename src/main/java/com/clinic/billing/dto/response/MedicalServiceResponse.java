package com.clinic.billing.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceResponse {
    private Long serviceId;
    private String serviceName;
    private BigDecimal price;
}