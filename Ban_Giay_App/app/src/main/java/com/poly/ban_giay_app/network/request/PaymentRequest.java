package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("payment_method")
    private String paymentMethod;

    // Thông tin thẻ (nếu thanh toán bằng thẻ)
    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("cardholder_name")
    private String cardholderName;

    @SerializedName("expiry_date")
    private String expiryDate;

    @SerializedName("cvv")
    private String cvv;

    // Thông tin chuyển khoản (nếu thanh toán ngân hàng)
    @SerializedName("transaction_code")
    private String transactionCode;

    @SerializedName("bank_name")
    private String bankName;

    public PaymentRequest(String orderId, String paymentMethod) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}







