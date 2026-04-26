package com.clinic.billing.dto.request;

import com.clinic.billing.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Status is required")
    private Status status;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    private String description;
}