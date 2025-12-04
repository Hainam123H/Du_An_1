package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

public class BankInfoResponse {
    @SerializedName("bank_name")
    private String bankName;

    @SerializedName("account_name")
    private String accountName;

    @SerializedName("account_number")
    private String accountNumber;

    @SerializedName("qr_code_url")
    private String qrCodeUrl;

    public String getBankName() {
        return bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }
}







