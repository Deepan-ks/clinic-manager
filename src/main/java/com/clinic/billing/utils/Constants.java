package com.clinic.billing.utils;

public class Constants {
    public static final String ACTIVE_STATUS = "ACTIVE";
    public static final String INACTIVE_STATUS = "INACTIVE";

    // Validation Messages
    public static final String NAME_REQUIRED = "Name is required";
    public static final String PHONE_REQUIRED = "Phone is required";
    public static final String INVALID_PHONE_FORMAT = "Phone number must be exactly 10 digits";
    public static final String AGE_NEGATIVE = "Age cannot be negative";
    public static final String GENDER_REQUIRED = "Gender is required";
    public static final String INVALID_EMAIL = "Email should be valid";
    public static final String SPECIALIZATION_ID_REQUIRED = "Specialization ID is required";
    public static final String STATUS_REQUIRED = "Status is required";
    public static final String PRICE_REQUIRED = "Price is required";
    public static final String SERVICE_ID_REQUIRED = "Service ID is required";
    public static final String QUANTITY_REQUIRED = "Quantity is required";
    public static final String QUANTITY_MIN = "Quantity must be at least 1";
    public static final String PATIENT_ID_REQUIRED = "Patient ID is required";
    public static final String DOCTOR_ID_REQUIRED = "Doctor ID is required";
    public static final String PAYMENT_MODE_REQUIRED = "Payment mode is required";
    public static final String BILL_ITEMS_REQUIRED = "At least one item is required";
    public static final String CANCEL_REASON_REQUIRED = "Reason for cancellation is required";
    public static final String SPECIALIZATION_NAME_REQUIRED = "Specialization name is required";
    public static final String PATIENT_ADDRESS_REQUIRED = "Patient address is required";
    public static final String DOCTOR_NAME_REQUIRED = "Doctor name is required";

    // Exception Messages
    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found";
    public static final String SPECIALIZATION_NOT_FOUND = "Specialization not found";
    public static final String SERVICE_NOT_FOUND = "Service not found";
    public static final String BILL_NOT_FOUND = "Bill not found";
    public static final String DOCTOR_SPECIALIZATION_MISMATCH = "Doctor does not belong to specialization";
    public static final String SERVICE_SPECIALIZATION_MISMATCH = "Service does not belong to specialization";
    public static final String SERVICE_INACTIVE = "Service status is INACTIVE";
    public static final String INVALID_DISCOUNT_PERCENT = "Discount cannot exceed 100%";
    public static final String INVALID_DISCOUNT_AMOUNT = "Discount exceeds subtotal";
    public static final String NEGATIVE_TOTAL = "Total cannot be negative";
    public static final String BILL_ALREADY_CANCELLED = "Bill already cancelled";
    public static final String INVOICE_GENERATION_FAILED = "Cannot generate invoice for cancelled bill";
    public static final String PRICE_MUST_BE_GREATER_THAN_ZERO = "Price must be greater than zero";


    // PDF Constants
    public static final String MEDISMILE_HEALTH_CARE = "MEDISMILE HEALTH CARE";
    public static final String SIGNATURE = "Authorized Signature";
    public static final String PATIENT_NAME = "Patient Name: ";
    public static final String AGE_GENDER = "Age & Gender: ";
    public static final String MOBILE = "Mobile: ";
    public static final String ADDRESS = "Address: ";
    public static final String BILL_TO = "BILL TO";
    public static final String INVOICE = "INVOICE";
    public static final String INVOICE_NO = "Invoice No: ";
    public static final String DATE = "Date: ";
    public static final String FULL_ADDRESS = "19/A, Rani Gardens, SIHS colony road, Singanallur, Coimbatore 641005 \n";
    public static final String EMAIL_PHONE = "Email: medismilehc@gmail.com     Phone: +91 9342639317";
    public static final String SL_NO = "Sl No";
    public static final String SERVICE = "Service";
    public static final String DESCRIPTION = "Description";
    public static final String PRICE = "Price";
    public static final String QUANTITY = "Qty";
    public static final String TOTAL = "Total";
    public static final String SUB_TOTAL = "Sub Total";
    public static final String DISCOUNT_IN_SYMBOL = "Discount (₹)";
    public static final String DISCOUNT_IN_PERCENT = "Discount   (%)";
    public static final String INVALID_PAYMENT_MODE = "Invalid payment mode";
    public static final String MEDICAL_SERVICE_NOT_FOUND = "Medical service not found";

    private Constants() {
    }
}
