package com.clinic.billing.service.impl;

import com.clinic.billing.entity.Bill;
import com.clinic.billing.entity.enums.BillStatus;
import com.clinic.billing.exception.ResourceNotFoundException;
import com.clinic.billing.repository.BillRepository;
import com.clinic.billing.service.InvoiceService;
import com.clinic.billing.service.PdfService;
import com.clinic.billing.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final BillRepository billRepository;
    private final PdfService pdfService;

    @Override
    public byte[] generateInvoice(Long billId) throws IOException {

        Bill bill = getBillById(billId);
        if (bill.getStatus() == BillStatus.CANCELLED) {
            throw new BadRequestException(Constants.INVOICE_GENERATION_FAILED);
        }
        ByteArrayOutputStream out = pdfService.buildPdf(bill);
        return out.toByteArray();
    }

    private Bill getBillById(Long billId) {
        return billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with id: " + billId));
    }

}
