package com.poly.ban_giay_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ImageButton btnBack = findViewById(R.id.btnBack);
        LinearLayout navAccount = findViewById(R.id.navAccount);

        btnBack.setOnClickListener(v -> onBackPressed());

        if (navAccount != null) {
            navAccount.setOnClickListener(v ->
                    startActivity(new Intent(PaymentMethodActivity.this, AccountActivity.class))
            );
        }
    }
}
