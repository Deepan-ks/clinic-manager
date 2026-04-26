package com.clinic.billing.dto.request;

import com.clinic.billing.entity.enums.Status;
import com.clinic.billing.utils.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalServiceRequest {

    @NotBlank(message = Constants.NAME_REQUIRED)
    private String name;

    @NotNull(message = Constants.PRICE_REQUIRED)
    private BigDecimal price;

    @NotNull(message = Constants.STATUS_REQUIRED)
    private Status status;

    @NotNull(message = Constants.SPECIALIZATION_ID_REQUIRED)
    private Long specializationId;

    private String description;
}