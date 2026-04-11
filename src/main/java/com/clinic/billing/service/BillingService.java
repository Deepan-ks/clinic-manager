package com.clinic.billing.service;

import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;

import java.util.List;

public interface BillingService {
    BillResponse createBill(CreateBillRequest bill);

    BillResponse getBillById(Long id);

    List<BillResponse> getAllBills();

    void cancelBill(Long id, CancelBillRequest cancelBillRequest);
}
