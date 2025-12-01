package com.poly.ban_giay_app;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poly.ban_giay_app.models.Product;

public class PaymentMethodActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ImageButton btnBack = findViewById(R.id.btnBack);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        TextView tvSelectedProduct = findViewById(R.id.tvSelectedProduct);

        btnBack.setOnClickListener(v -> onBackPressed());

        if (navAccount != null) {
            navAccount.setOnClickListener(v ->
                    startActivity(new Intent(PaymentMethodActivity.this, AccountActivity.class))
            );
        }

        Product product = (Product) getIntent().getSerializableExtra("product");
        if (product != null && tvSelectedProduct != null) {
            String info = product.name;
            if (product.priceNew != null && !product.priceNew.isEmpty()) {
                info += " • " + product.priceNew;
            }
            tvSelectedProduct.setText(info);
            tvSelectedProduct.setVisibility(View.VISIBLE);
        }
    }
}
