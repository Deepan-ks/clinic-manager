package com.clinic.billing.dto.request;

import com.clinic.billing.entity.enums.Status;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceRequest {

    private String name;
    private BigDecimal price;
    private Status status;
    private Long specializationId;
    private String description;
}