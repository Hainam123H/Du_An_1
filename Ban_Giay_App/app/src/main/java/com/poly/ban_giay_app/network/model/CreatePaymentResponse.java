package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Phản hồi khi gọi API tạo đơn thanh toán (/api/payment/create-payment)
 */
public class CreatePaymentResponse {

    @SerializedName("paymentId")
    private String paymentId;

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("method")
    private String method;

    @SerializedName("amount")
    private Integer amount;

    @SerializedName("status")
    private String status;

    public String getPaymentId() {
        return paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMethod() {
        return method;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }
}


