package com.clinic.billing.service;

import java.io.IOException;
import java.net.MalformedURLException;

public interface InvoiceService {
    byte[] generateInvoice(Long billId) throws IOException;
}
