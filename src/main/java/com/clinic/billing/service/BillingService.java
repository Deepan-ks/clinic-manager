package com.clinic.billing.service;

import com.clinic.billing.dto.request.CancelBillRequest;
import com.clinic.billing.dto.request.CreateBillRequest;
import com.clinic.billing.dto.response.BillResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BillingService {
    BillResponse createBill(CreateBillRequest bill);

    BillResponse getBillById(Long id);

    List<BillResponse> getAllBills();

    /** Paginated list with optional search and date range */
    Page<BillResponse> getBills(String search, LocalDate fromDate, LocalDate toDate, Pageable pageable);

    void cancelBill(Long id, CancelBillRequest cancelBillRequest);
}
