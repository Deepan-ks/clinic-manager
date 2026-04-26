package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelBillRequest {

    @NotBlank(message = "Reason for cancellation is required")
    private String reason;
}
