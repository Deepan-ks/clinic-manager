package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSpecializationRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Status is required")
    private String status;
}