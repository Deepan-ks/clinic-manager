package com.clinic.billing.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.clinic.billing.utils.Constants;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillItemRequest {

    @NotNull(message = Constants.SERVICE_ID_REQUIRED)
    private Long serviceId;

    @NotNull(message = Constants.QUANTITY_REQUIRED)
    @Min(value = 1, message = Constants.QUANTITY_MIN)
    private Integer quantity;
}