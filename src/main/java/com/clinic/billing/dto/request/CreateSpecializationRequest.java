package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpecializationRequest {

    @NotBlank(message = Constants.SPECIALIZATION_NAME_REQUIRED)
    private String name;

    @NotBlank(message = Constants.STATUS_REQUIRED)
    private String status;
}