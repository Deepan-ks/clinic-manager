package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Specialization ID is required")
    private Long specializationId;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Status is required")
    private String status;
}