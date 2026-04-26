package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorRequest {

    @NotBlank(message = Constants.NAME_REQUIRED)
    private String name;

    @NotNull(message = Constants.SPECIALIZATION_ID_REQUIRED)
    private Long specializationId;

    @NotBlank(message = Constants.PHONE_REQUIRED)
    private String phone;

    @NotBlank(message = Constants.STATUS_REQUIRED)
    private String status;
}