# Kiểm tra tương thích API - Ban Giay App

## Tổng quan
File này liệt kê các API endpoints và cấu trúc dữ liệu mà app Android đang sử dụng, để đối chiếu với backend API.

## 1. ORDER API - Tạo đơn hàng

### Endpoint: `POST /api/order`

### Request Body (OrderRequest):
```json
{
  "items": [
    {
      "product_id": "string",      // ID sản phẩm từ MongoDB (_id)
      "quantity": 1,                 // Số lượng
      "size": "37",                  // Kích thước (37, 38, 39)
      "price": 500000                // Giá (số nguyên, không có dấu phẩy)
    }
  ],
  "payment_method": "credit_card",   // "credit_card", "atm_card", "bank_transfer"
  "shipping_address": "string",      // Địa chỉ giao hàng
  "phone": "string",                 // Số điện thoại
  "note": "string"                   // Ghi chú (optional)
}
```

### Response (BaseResponse<OrderResponse>):
```json
{
  "success": true,
  "message": "string",
  "data": {
    "_id": "string",
    "order_id": "string",
    "user_id": "string",
    "items": [
      {
        "product_id": "string",
        "product_name": "string",
        "quantity": 1,
        "price": 500000,
        "size": "37"
      }
    ],
    "total_amount": 500000,
    "payment_method": "credit_card",
    "payment_status": "pending",
    "order_status": "pending",
    "shipping_address": "string",
    "phone": "string",
    "created_at": "string",
    "updated_at": "string"
  }
}
```

## 2. PAYMENT API - Xử lý thanh toán

### Endpoint: `POST /api/payment`

### Request Body (PaymentRequest):
```json
{
  "order_id": "string",              // ID đơn hàng vừa tạo
  "payment_method": "credit_card",   // "credit_card", "atm_card", "bank_transfer"
  
  // Nếu thanh toán bằng thẻ:
  "card_number": "string",           // Số thẻ (không có khoảng trắng)
  "cardholder_name": "string",       // Tên chủ thẻ
  "expiry_date": "MM/YY",           // Ngày hết hạn (format: MM/YY)
  "cvv": "string",                  // CVV
  
  // Nếu thanh toán chuyển khoản:
  "transaction_code": "string",      // Mã giao dịch
  "bank_name": "string"             // Tên ngân hàng
}
```

### Response (BaseResponse<OrderResponse>):
```json
{
  "success": true,
  "message": "string",
  "data": {
    // Cùng cấu trúc như OrderResponse ở trên
    // payment_status sẽ được cập nhật thành "paid" hoặc "completed"
  }
}
```

## 3. Authentication

### Header yêu cầu:
```
Authorization: Bearer {token}
```

Token được lấy từ API login và lưu trong SessionManager.

## 4. Các điểm quan trọng cần kiểm tra:

### ✅ Đã khớp:
- Endpoint `/api/order` (POST)
- Endpoint `/api/payment` (POST)
- Cấu trúc OrderRequest với items, payment_method, shipping_address, phone
- Cấu trúc PaymentRequest với order_id, payment_method
- Header Authorization với Bearer token

### ⚠️ Cần kiểm tra backend:
1. **OrderRequest.items[].price**: App gửi giá dạng số nguyên (Integer), không có dấu phẩy
   - Ví dụ: 500000 thay vì "500,000₫"
   - Backend cần nhận Integer, không phải String

2. **OrderRequest.items[].product_id**: App gửi `_id` từ MongoDB
   - Cần đảm bảo Product model trong app có field `id` được lưu từ ProductResponse

3. **PaymentRequest.payment_method**: Các giá trị có thể:
   - "credit_card"
   - "atm_card" 
   - "bank_transfer"
   - Backend cần hỗ trợ các giá trị này

4. **Response format**: App mong đợi BaseResponse với cấu trúc:
   ```json
   {
     "success": true/false,
     "message": "string",
     "data": { ... }  // hoặc "product", "products", "user" tùy endpoint
   }
   ```

5. **Error handling**: App sử dụng BaseResponse.success để kiểm tra thành công
   - Nếu success = false, sẽ hiển thị message

## 5. Các API khác đang sử dụng:

### Auth:
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/forgot-password`

### Product:
- `GET /api/product`
- `GET /api/product/{id}`
- `GET /api/product/best-selling`
- `GET /api/product/newest`
- `GET /api/product/category/{danh_muc}`

### Payment:
- `GET /api/payment/bank-info` - Lấy thông tin ngân hàng

### Order:
- `GET /api/order` - Lấy danh sách đơn hàng của user
- `GET /api/order/{id}` - Lấy chi tiết đơn hàng
- `PUT /api/order/{id}/cancel` - Hủy đơn hàng

## 6. Lưu ý khi test:

1. **Tạo đơn hàng**: Cần đảm bảo:
   - User đã đăng nhập (có token)
   - Product có `id` hợp lệ từ MongoDB
   - Price được parse từ String sang Integer (bỏ dấu phẩy và ký tự đặc biệt)

2. **Thanh toán**: Cần đảm bảo:
   - Order đã được tạo thành công (có order_id)
   - Payment method khớp với order đã tạo
   - Thông tin thẻ/chuyển khoản được validate

3. **Authentication**: Token được thêm vào header tự động qua ApiClient interceptor





