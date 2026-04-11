package com.clinic.billing.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateBillRequest {
    private Long patientId;
    private String doctorName;
    private String paymentMode;
    private String notes;
    private List<BillItemRequest> items;
}
