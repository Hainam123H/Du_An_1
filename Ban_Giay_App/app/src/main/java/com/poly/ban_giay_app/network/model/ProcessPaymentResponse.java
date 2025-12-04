package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Phản hồi khi xử lý thanh toán thẻ tín dụng (/api/payment/process-credit-card)
 */
public class ProcessPaymentResponse {

    @SerializedName("paymentId")
    private String paymentId;

    @SerializedName("status")
    private String status;

    @SerializedName("method")
    private String method;

    @SerializedName("amount")
    private Integer amount;

    public String getPaymentId() {
        return paymentId;
    }

    public String getStatus() {
        return status;
    }

    public String getMethod() {
        return method;
    }

    public Integer getAmount() {
        return amount;
    }
}


