package com.poly.ban_giay_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poly.ban_giay_app.models.Product;
import com.poly.ban_giay_app.network.ApiClient;
import com.poly.ban_giay_app.network.ApiService;
import com.poly.ban_giay_app.network.NetworkUtils;
import com.poly.ban_giay_app.network.model.BaseResponse;
import com.poly.ban_giay_app.network.model.OrderResponse;
import com.poly.ban_giay_app.network.model.CreatePaymentResponse;
import com.poly.ban_giay_app.network.model.ProcessPaymentResponse;
import com.poly.ban_giay_app.network.request.OrderRequest;
import com.poly.ban_giay_app.network.request.CreatePaymentRequest;
import com.poly.ban_giay_app.network.request.CreditCardPaymentRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreditCardActivity extends AppCompatActivity {

    private EditText etCardholderName, etCardNumber, etExpiryDate, etCVV;
    private Button btnSubmit;
    private Product product;
    private String selectedSize;
    private int quantity;
    private SessionManager sessionManager;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        // Initialize
        ApiClient.initialize(this);
        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");

        // Get product from intent
        product = (Product) getIntent().getSerializableExtra("product");
        selectedSize = getIntent().getStringExtra("selectedSize");
        quantity = getIntent().getIntExtra("quantity", 1);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        bindActions();
        setupCardNumberFormatting();
        setupExpiryDateFormatting();
    }

    private void initViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvBack = findViewById(R.id.tvBack);
        etCardholderName = findViewById(R.id.etCardholderName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCVV = findViewById(R.id.etCVV);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Back button
        View.OnClickListener backListener = v -> finish();
        btnBack.setOnClickListener(backListener);
        if (tvBack != null) {
            tvBack.setOnClickListener(backListener);
        }
    }

    private void bindActions() {
        btnSubmit.setOnClickListener(v -> {
            if (validateInput()) {
                createOrderAndProcessPayment();
            }
        });
    }

    private void createOrderAndProcessPayment() {
        if (product == null || product.id == null || product.id.isEmpty()) {
            Toast.makeText(this, "Thông tin sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        // Parse price từ String sang Integer (bỏ dấu phẩy và ký tự đặc biệt)
        Integer price = parsePrice(product.priceNew);
        if (price == null || price <= 0) {
            progressDialog.dismiss();
            Toast.makeText(this, "Giá sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo OrderItem
        List<OrderRequest.OrderItem> items = new ArrayList<>();
        items.add(new OrderRequest.OrderItem(product.id, quantity, selectedSize, price));

        // Tạo OrderRequest (tạm thời dùng địa chỉ và số điện thoại mặc định)
        String shippingAddress = "Chưa cập nhật"; // Có thể lấy từ user profile sau
        String phone = "Chưa cập nhật"; // Có thể lấy từ user profile sau
        OrderRequest orderRequest = new OrderRequest(items, "credit_card", shippingAddress, phone);

        // Tạo đơn hàng
        apiService.createOrder(orderRequest).enqueue(new Callback<BaseResponse<OrderResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<OrderResponse>> call, Response<BaseResponse<OrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<OrderResponse> body = response.body();
                    if (body.getSuccess() && body.getData() != null) {
                        OrderResponse order = body.getData();
                        // Tạo bản ghi Payment trong MongoDB rồi mới xử lý thẻ
                        createPayment(order);
                    } else {
                        progressDialog.dismiss();
                        String errorMsg = body.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Không thể tạo đơn hàng";
                        }
                        Toast.makeText(CreditCardActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    String errorMsg = NetworkUtils.extractErrorMessage(response);
                    Toast.makeText(CreditCardActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<OrderResponse>> call, Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "Không thể kết nối máy chủ.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Unable to resolve host") || t.getMessage().contains("failed to connect")) {
                        errorMsg = "Không thể kết nối máy chủ. Vui lòng kiểm tra:\n1. Server API đang chạy tại http://10.0.2.2:3000\n2. Emulator có kết nối mạng";
                    } else {
                        errorMsg = "Lỗi kết nối: " + t.getMessage();
                    }
                }
                Toast.makeText(CreditCardActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createPayment(OrderResponse order) {
        // Lấy tên chủ thẻ từ input field
        String fullName = etCardholderName.getText().toString().trim();
        if (fullName.isEmpty()) {
            fullName = "Chưa cập nhật";
        }
        
        // Lấy email từ user đã đăng nhập
        String email = sessionManager.getEmail();
        if (email == null || email.isEmpty()) {
            email = "chua_cap_nhat@example.com";
        }
        
        // Lấy phone number, nếu là "Chưa cập nhật" thì dùng số thẻ
        String phone = order.getPhone() != null ? order.getPhone() : "Chưa cập nhật";
        if ("Chưa cập nhật".equals(phone)) {
            // Lấy số thẻ từ input field (bỏ khoảng trắng)
            phone = etCardNumber.getText().toString().replaceAll(" ", "").trim();
            if (phone.isEmpty()) {
                phone = "Chưa cập nhật";
            }
        }

        CreatePaymentRequest request = new CreatePaymentRequest(
                order.getId(),             // orderId trong Payment model
                order.getUserId(),         // userId
                fullName,
                email,
                phone,
                "credit_card",
                order.getTotalAmount()     // amount
        );

        apiService.createPayment(request).enqueue(new Callback<BaseResponse<CreatePaymentResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<CreatePaymentResponse>> call, Response<BaseResponse<CreatePaymentResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    CreatePaymentResponse data = response.body().getData();
                    if (data != null && data.getPaymentId() != null) {
                        // Sau khi đã có paymentId (đã lưu vào Mongo), tiếp tục xử lý thẻ
                        processCreditCard(data.getPaymentId());
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CreditCardActivity.this, "Không lấy được paymentId", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    String errorMsg = NetworkUtils.extractErrorMessage(response);
                    Toast.makeText(CreditCardActivity.this, "Lỗi tạo thanh toán: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<CreatePaymentResponse>> call, Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "Không thể kết nối máy chủ khi tạo thanh toán.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Unable to resolve host") || t.getMessage().contains("failed to connect")) {
                        errorMsg = "Không thể kết nối máy chủ. Vui lòng kiểm tra server API đang chạy tại http://localhost:3000";
                    } else {
                        errorMsg = "Lỗi: " + t.getMessage();
                    }
                }
                Toast.makeText(CreditCardActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processCreditCard(String paymentId) {
        String cardNumber = etCardNumber.getText().toString().replaceAll(" ", "").trim();
        String cardholderName = etCardholderName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        CreditCardPaymentRequest paymentRequest = new CreditCardPaymentRequest(
                paymentId,
                cardNumber,
                expiryDate,
                cvv,
                cardholderName
        );

        apiService.processCreditCard(paymentRequest).enqueue(new Callback<BaseResponse<ProcessPaymentResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<ProcessPaymentResponse>> call, Response<BaseResponse<ProcessPaymentResponse>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    BaseResponse<ProcessPaymentResponse> body = response.body();
                    if (body.getSuccess()) {
                        Toast.makeText(CreditCardActivity.this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                        // Quay về màn hình chính
                        Intent intent = new Intent(CreditCardActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = body.getMessage();
                        if (errorMsg == null || errorMsg.isEmpty()) {
                            errorMsg = "Thanh toán thất bại";
                        }
                        Toast.makeText(CreditCardActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = NetworkUtils.extractErrorMessage(response);
                    Toast.makeText(CreditCardActivity.this, "Lỗi: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<ProcessPaymentResponse>> call, Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "Không thể kết nối máy chủ.";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("Unable to resolve host") || t.getMessage().contains("failed to connect")) {
                        errorMsg = "Không thể kết nối máy chủ. Vui lòng kiểm tra server API đang chạy.";
                    } else {
                        errorMsg = "Lỗi kết nối: " + t.getMessage();
                    }
                }
                Toast.makeText(CreditCardActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Integer parsePrice(String priceString) {
        if (priceString == null || priceString.isEmpty()) {
            return null;
        }
        try {
            // Loại bỏ tất cả ký tự không phải số
            String cleanPrice = priceString.replaceAll("[^0-9]", "");
            if (cleanPrice.isEmpty()) {
                return null;
            }
            return Integer.parseInt(cleanPrice);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setupCardNumberFormatting() {
        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().replaceAll(" ", "");
                if (input.length() > 0) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < input.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(input.charAt(i));
                    }
                    if (!formatted.toString().equals(s.toString())) {
                        etCardNumber.removeTextChangedListener(this);
                        etCardNumber.setText(formatted.toString());
                        etCardNumber.setSelection(formatted.length());
                        etCardNumber.addTextChangedListener(this);
                    }
                }
            }
        });
    }

    private void setupExpiryDateFormatting() {
        etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().replaceAll("/", "");
                if (input.length() >= 2 && !s.toString().contains("/")) {
                    String formatted = input.substring(0, 2) + "/" + (input.length() > 2 ? input.substring(2) : "");
                    etExpiryDate.removeTextChangedListener(this);
                    etExpiryDate.setText(formatted);
                    etExpiryDate.setSelection(formatted.length());
                    etExpiryDate.addTextChangedListener(this);
                }
            }
        });
    }

    private boolean validateInput() {
        String cardholderName = etCardholderName.getText().toString().trim();
        String cardNumber = etCardNumber.getText().toString().replaceAll(" ", "").trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String cvv = etCVV.getText().toString().trim();

        if (cardholderName.isEmpty()) {
            etCardholderName.setError("Vui lòng nhập tên chủ thẻ");
            etCardholderName.requestFocus();
            return false;
        }

        if (cardNumber.isEmpty() || cardNumber.length() < 13 || cardNumber.length() > 19) {
            etCardNumber.setError("Số thẻ không hợp lệ");
            etCardNumber.requestFocus();
            return false;
        }

        if (expiryDate.isEmpty() || !expiryDate.matches("\\d{2}/\\d{2}")) {
            etExpiryDate.setError("Ngày hết hạn không hợp lệ (MM/YY)");
            etExpiryDate.requestFocus();
            return false;
        }

        if (cvv.isEmpty() || cvv.length() < 3 || cvv.length() > 4) {
            etCVV.setError("CVV không hợp lệ");
            etCVV.requestFocus();
            return false;
        }

        return true;
    }
}

