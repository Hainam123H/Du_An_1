package com.poly.ban_giay_app;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poly.ban_giay_app.models.Product;

import java.text.NumberFormat;
import java.util.Locale;

public class PaymentMethodActivity extends AppCompatActivity {

    private Product product;
    private int quantity = 1;
    private String selectedSize = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvBack = findViewById(R.id.tvBack);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        LinearLayout layoutCreditCard = findViewById(R.id.layoutCreditCard);
        LinearLayout layoutAtmCard = findViewById(R.id.layoutAtmCard);
        LinearLayout layoutBankTransfer = findViewById(R.id.layoutBankTransfer);
        TextView tvSelectedProduct = findViewById(R.id.tvSelectedProduct);

        // Lấy thông tin từ intent
        product = (Product) getIntent().getSerializableExtra("product");
        quantity = getIntent().getIntExtra("quantity", 1);
        selectedSize = getIntent().getStringExtra("selectedSize");

        // Nút back trên giao diện & chữ "Quay lại": về màn trước (chi tiết sản phẩm)
        View.OnClickListener backListener = v -> finish();
        btnBack.setOnClickListener(backListener);
        if (tvBack != null) {
            tvBack.setOnClickListener(backListener);
        }

        if (navAccount != null) {
            navAccount.setOnClickListener(v ->
                    startActivity(new Intent(PaymentMethodActivity.this, AccountActivity.class))
            );
        }

        // Bấm vào "Thẻ tín dụng" -> chuyển sang màn nhập thông tin thẻ
        if (layoutCreditCard != null) {
            layoutCreditCard.setOnClickListener(v -> {
                Intent intent = new Intent(PaymentMethodActivity.this, CreditCardActivity.class);
                if (product != null) {
                    intent.putExtra("product", product);
                }
                intent.putExtra("quantity", quantity);
                intent.putExtra("selectedSize", selectedSize);
                startActivity(intent);
            });
        }

        // Bấm vào "Thẻ ATM" -> chuyển sang màn nhập thông tin thẻ ATM
        if (layoutAtmCard != null) {
            layoutAtmCard.setOnClickListener(v -> {
                Intent intent = new Intent(PaymentMethodActivity.this, AtmCardActivity.class);
                if (product != null) {
                    intent.putExtra("product", product);
                }
                intent.putExtra("quantity", quantity);
                intent.putExtra("selectedSize", selectedSize);
                startActivity(intent);
            });
        }

        // Bấm vào "Thanh toán ngân hàng" -> chuyển sang màn QR code và thông tin tài khoản
        if (layoutBankTransfer != null) {
            layoutBankTransfer.setOnClickListener(v -> {
                Intent intent = new Intent(PaymentMethodActivity.this, BankTransferActivity.class);
                if (product != null) {
                    intent.putExtra("product", product);
                }
                intent.putExtra("quantity", quantity);
                intent.putExtra("selectedSize", selectedSize);
                startActivity(intent);
            });
        }

        // Hiển thị thông tin sản phẩm với tổng tiền (giá * số lượng)
        if (product != null && tvSelectedProduct != null) {
            String info = product.name;
            if (product.priceNew != null && !product.priceNew.isEmpty()) {
                // Tính tổng tiền = giá * số lượng
                Integer unitPrice = parsePrice(product.priceNew);
                if (unitPrice != null && unitPrice > 0) {
                    int totalPrice = unitPrice * quantity;
                    String totalPriceFormatted = formatPrice(totalPrice);
                    info += " • " + totalPriceFormatted;
                } else {
                    info += " • " + product.priceNew;
                }
            }
            tvSelectedProduct.setText(info);
            tvSelectedProduct.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Parse giá từ String (có thể có dấu phẩy, dấu chấm, ký tự đặc biệt) sang Integer
     */
    private Integer parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return null;
        }
        try {
            // Loại bỏ tất cả ký tự không phải số
            String cleaned = priceStr.replaceAll("[^0-9]", "");
            if (cleaned.isEmpty()) {
                return null;
            }
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Format giá thành chuỗi có dấu phẩy (ví dụ: 1.200.000₫)
     */
    private String formatPrice(int price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        return formatter.format(price) + "₫";
    }

}
