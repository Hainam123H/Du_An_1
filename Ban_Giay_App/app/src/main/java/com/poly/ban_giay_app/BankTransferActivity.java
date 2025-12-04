package com.poly.ban_giay_app;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.poly.ban_giay_app.models.Product;

import java.util.Hashtable;

public class BankTransferActivity extends AppCompatActivity {

    private ImageView imgQRCode;
    private TextView tvBankName, tvAccountName, tvAccountNumber, tvAmount;
    private Button btnCopyAccount;
    private Product product;
    private int quantity = 1;

    // Thông tin ngân hàng (có thể lấy từ config hoặc API)
    private static final String BANK_NAME = "Vietcombank";
    private static final String ACCOUNT_NAME = "CONG TY TNHH SNEAKER UNIVERSE";
    private static final String ACCOUNT_NUMBER = "1234567890";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_transfer);

        // Get product from intent
        product = (Product) getIntent().getSerializableExtra("product");
        quantity = getIntent().getIntExtra("quantity", 1);

        initViews();
        bindActions();
        displayBankInfo();
        generateQRCode();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvBack = findViewById(R.id.tvBack);
        imgQRCode = findViewById(R.id.imgQRCode);
        tvBankName = findViewById(R.id.tvBankName);
        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvAmount = findViewById(R.id.tvAmount);
        btnCopyAccount = findViewById(R.id.btnCopyAccount);

        // Back button
        View.OnClickListener backListener = v -> finish();
        btnBack.setOnClickListener(backListener);
        if (tvBack != null) {
            tvBack.setOnClickListener(backListener);
        }
    }

    private void bindActions() {
        btnCopyAccount.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Số tài khoản", ACCOUNT_NUMBER);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép số tài khoản", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayBankInfo() {
        tvBankName.setText(BANK_NAME);
        tvAccountName.setText(ACCOUNT_NAME);
        tvAccountNumber.setText(ACCOUNT_NUMBER);

        // Hiển thị tổng tiền = giá * số lượng
        if (product != null && product.priceNew != null && !product.priceNew.isEmpty()) {
            Integer unitPrice = parsePrice(product.priceNew);
            if (unitPrice != null && unitPrice > 0) {
                int totalPrice = unitPrice * quantity;
                tvAmount.setText(formatPrice(totalPrice));
            } else {
                tvAmount.setText(product.priceNew);
            }
        } else {
            tvAmount.setText("0₫");
        }
    }

    private void generateQRCode() {
        // Tạo nội dung QR code (có thể chứa thông tin chuyển khoản)
        String qrContent = createQRContent();
        
        try {
            QRCodeWriter writer = new QRCodeWriter();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            imgQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private String createQRContent() {
        // Tạo nội dung QR code theo format VietQR hoặc format tùy chỉnh
        // Format: bank|accountNumber|accountName|amount|content
        String amount = "0";
        if (product != null && product.priceNew != null && !product.priceNew.isEmpty()) {
            // Tính tổng tiền = giá * số lượng
            Integer unitPrice = parsePrice(product.priceNew);
            if (unitPrice != null && unitPrice > 0) {
                int totalPrice = unitPrice * quantity;
                amount = String.valueOf(totalPrice);
            } else {
                // Fallback: loại bỏ ký tự không phải số
                amount = product.priceNew.replaceAll("[^0-9]", "");
            }
        }
        
        String content = "Thanh toan don hang " + (product != null ? product.name : "");
        
        // Format VietQR hoặc format đơn giản
        return String.format("%s|%s|%s|%s|%s", 
            BANK_NAME, 
            ACCOUNT_NUMBER, 
            ACCOUNT_NAME, 
            amount,
            content);
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
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.getDefault());
        return formatter.format(price) + "₫";
    }
}

