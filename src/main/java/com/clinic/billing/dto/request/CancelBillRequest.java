package com.clinic.billing.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelBillRequest {

    @NotBlank(message = Constants.CANCEL_REASON_REQUIRED)
    private String reason;
}
