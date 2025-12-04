package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

/**
 * Body gửi lên API /api/payment/create-payment
 * Các field đặt theo đúng tên mà backend destructuring trong payment.controller.js
 */
public class CreatePaymentRequest {

    @SerializedName("orderId")
    private String orderId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("method")
    private String method; // "credit_card", "atm", "cod"

    @SerializedName("amount")
    private Integer amount;

    public CreatePaymentRequest(String orderId,
                                String userId,
                                String fullName,
                                String email,
                                String phoneNumber,
                                String method,
                                Integer amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.method = method;
        this.amount = amount;
    }
}


