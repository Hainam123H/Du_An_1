package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

/**
 * Body gửi lên API /api/payment/process-credit-card
 */
public class CreditCardPaymentRequest {

    @SerializedName("paymentId")
    private String paymentId;

    @SerializedName("cardNumber")
    private String cardNumber;

    @SerializedName("cardExpiry")
    private String cardExpiry;

    @SerializedName("cvv")
    private String cvv;

    @SerializedName("cardHolder")
    private String cardHolder;

    public CreditCardPaymentRequest(String paymentId,
                                    String cardNumber,
                                    String cardExpiry,
                                    String cvv,
                                    String cardHolder) {
        this.paymentId = paymentId;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cvv = cvv;
        this.cardHolder = cardHolder;
    }
}


