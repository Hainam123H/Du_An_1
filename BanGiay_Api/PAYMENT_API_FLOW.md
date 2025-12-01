# 🛍️ HƯỚNG DẪN PAYMENT API - MUA NGAY GIÀY

## 📱 Flow Ứng Dụng

```
Trang Sản Phẩm
      ↓
   [MUA NGAY]
      ↓
Trang Quá Lộc (Quay lại)
      ↓
   Chọn Phương Thức Thanh Toán
   ├─ Thẻ Tín Dụng
   ├─ Thẻ ATM
   └─ Thanh Toán Khi Nhận Hàng (COD)
      ↓
   Xác Nhận & Thanh Toán
```

---

## 🎯 3 Phương Thức Thanh Toán

### 1️⃣ Thẻ Tín Dụng (Credit Card)
- **Icon:** Hình thẻ tín dụng
- **Endpoint:** `POST /api/payment/process-credit-card`
- **Thông tin cần:** Số thẻ, ngày hết hạn, CVV, tên chủ thẻ
- **Trạng thái:** pending → processing → completed

### 2️⃣ Thẻ ATM
- **Icon:** Hình ATM
- **Endpoint:** `POST /api/payment/process-atm`
- **Thông tin cần:** Mã ngân hàng, số tài khoản, tên ngân hàng
- **Trạng thái:** pending → processing → completed

### 3️⃣ Thanh Toán Khi Nhận Hàng (COD)
- **Icon:** Hình nhân viên giao hàng
- **Endpoint:** `POST /api/payment/process-cod`
- **Thông tin cần:** Địa chỉ giao hàng, ghi chú
- **Trạng thái:** pending (hoàn thành khi giao hàng)

---

## 📡 API Endpoints

### 1. Tạo Đơn Thanh Toán
```
POST /api/payment/create-payment
```

**Request:**
```json
{
  "orderId": "507f191e810c19729de860ea",
  "userId": "507f1f77bcf86cd799439011",
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phoneNumber": "0981234567",
  "method": "cod",
  "amount": 500000
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tạo đơn thanh toán thành công",
  "data": {
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "transactionId": "PAY_1701388800000_abc12def",
    "method": "cod",
    "amount": 500000,
    "status": "pending",
    "payment": { ... }
  }
}
```

---

### 2. Thanh Toán Bằng Thẻ Tín Dụng
```
POST /api/payment/process-credit-card
```

**Request:**
```json
{
  "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
  "cardNumber": "4532015112830366",
  "cardExpiry": "12/25",
  "cvv": "123",
  "cardHolder": "NGUYEN VAN A"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đang xử lý thanh toán bằng thẻ tín dụng",
  "data": {
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "processing",
    "method": "credit_card",
    "amount": 500000
  }
}
```

---

### 3. Thanh Toán Bằng Thẻ ATM
```
POST /api/payment/process-atm
```

**Request:**
```json
{
  "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
  "atmBankCode": "VIETCOMBANK",
  "atmBankName": "Vietcombank",
  "atmAccountNumber": "12345678901234"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đang xử lý thanh toán bằng thẻ ATM",
  "data": {
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "processing",
    "method": "atm",
    "amount": 500000,
    "bankCode": "VIETCOMBANK"
  }
}
```

---

### 4. Thanh Toán Khi Nhận Hàng (COD)
```
POST /api/payment/process-cod
```

**Request:**
```json
{
  "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
  "deliveryAddress": "123 Nguyễn Huệ, Q.1, TP.HCM",
  "notes": "Giao hàng vào ngày hôm sau, trước 17h"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Đơn hàng sẽ được thanh toán khi nhận hàng",
  "data": {
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "pending",
    "method": "cod",
    "amount": 500000,
    "deliveryAddress": "123 Nguyễn Huệ, Q.1, TP.HCM"
  }
}
```

---

### 5. Lấy Chi Tiết Thanh Toán
```
GET /api/payment/:paymentId
```

**Example:**
```
GET /api/payment/64f1a2b3c4d5e6f7g8h9i0j3
```

**Response:**
```json
{
  "success": true,
  "data": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "orderId": { "_id": "507f191e810c19729de860ea", "tong_tien": 500000 },
    "userId": { "_id": "507f1f77bcf86cd799439011", "fullName": "Nguyễn Văn A" },
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0981234567",
    "method": "cod",
    "amount": 500000,
    "status": "pending",
    "transactionId": "PAY_1701388800000_abc12def",
    "paymentDetails": { "deliveryAddress": "123 Nguyễn Huệ, Q.1, TP.HCM" },
    "createdAt": "2024-12-01T10:00:00.000Z",
    "updatedAt": "2024-12-01T10:00:00.000Z"
  }
}
```

---

### 6. Xác Nhận Thanh Toán
```
PUT /api/payment/:paymentId/confirm
```

**Response:**
```json
{
  "success": true,
  "message": "Thanh toán đã được xác nhận",
  "data": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "completed",
    "updatedAt": "2024-12-01T10:05:00.000Z"
  }
}
```

---

### 7. Hủy Thanh Toán
```
PUT /api/payment/:paymentId/cancel
```

**Request:**
```json
{
  "reason": "Khách hàng yêu cầu hủy"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Thanh toán đã bị hủy",
  "data": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "cancelled",
    "notes": "Khách hàng yêu cầu hủy"
  }
}
```

---

### 8. Lấy Danh Sách Thanh Toán
```
GET /api/payment?userId=...&status=...&method=...&page=1&limit=10
```

**Query Parameters:**
- `userId` (tùy chọn): Lọc theo user
- `status` (tùy chọn): pending, processing, completed, failed, cancelled
- `method` (tùy chọn): credit_card, atm, cod
- `page` (mặc định: 1)
- `limit` (mặc định: 10)

**Response:**
```json
{
  "success": true,
  "data": {
    "total": 25,
    "page": 1,
    "pages": 3,
    "payments": [ ... ]
  }
}
```

---

### 9. Lấy Thống Kê Thanh Toán
```
GET /api/payment/stats/overview
```

**Response:**
```json
{
  "success": true,
  "data": {
    "byStatus": [
      {
        "_id": "pending",
        "count": 5,
        "totalAmount": 2500000
      },
      {
        "_id": "completed",
        "count": 15,
        "totalAmount": 7500000
      }
    ],
    "byMethod": [
      {
        "_id": "cod",
        "count": 12,
        "totalAmount": 6000000
      },
      {
        "_id": "credit_card",
        "count": 5,
        "totalAmount": 2500000
      },
      {
        "_id": "atm",
        "count": 3,
        "totalAmount": 1500000
      }
    ],
    "totalRevenue": [ { "_id": null, "total": 7500000 } ]
  }
}
```

---

## 🧪 Cách Test

### Cách 1: Sử dụng File Test
```bash
node test-payment-flow.js
```

### Cách 2: Sử dụng cURL
```bash
# Tạo thanh toán
curl -X POST http://localhost:3000/api/payment/create-payment \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "507f191e810c19729de860ea",
    "userId": "507f1f77bcf86cd799439011",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0981234567",
    "method": "cod",
    "amount": 500000
  }'

# Thanh toán bằng thẻ tín dụng
curl -X POST http://localhost:3000/api/payment/process-credit-card \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "cardNumber": "4532015112830366",
    "cardExpiry": "12/25",
    "cvv": "123",
    "cardHolder": "NGUYEN VAN A"
  }'

# Thanh toán bằng ATM
curl -X POST http://localhost:3000/api/payment/process-atm \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "atmBankCode": "VIETCOMBANK",
    "atmBankName": "Vietcombank",
    "atmAccountNumber": "12345678901234"
  }'

# Thanh toán COD
curl -X POST http://localhost:3000/api/payment/process-cod \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "64f1a2b3c4d5e6f7g8h9i0j3",
    "deliveryAddress": "123 Nguyễn Huệ, Q.1, TP.HCM"
  }'

# Lấy chi tiết
curl http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3

# Xác nhận thanh toán
curl -X PUT http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3/confirm

# Hủy thanh toán
curl -X PUT http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3/cancel \
  -H "Content-Type: application/json" \
  -d '{"reason": "Khách hủy"}'
```

### Cách 3: Sử dụng Postman
Tạo collection Payment với các request trên

---

## 📊 MongoDB Schema

### Payment Collection
```javascript
{
  _id: ObjectId,
  orderId: ObjectId,        // Liên kết Order
  userId: ObjectId,         // Liên kết User
  fullName: String,         // Tên khách hàng
  email: String,            // Email khách hàng
  phoneNumber: String,      // SĐT khách hàng
  method: String,           // credit_card | atm | cod
  amount: Number,           // Số tiền
  status: String,           // pending | processing | completed | failed | cancelled
  transactionId: String,    // ID giao dịch duy nhất
  paymentDetails: {         // Chi tiết theo phương thức
    // Cho credit_card:
    cardLastFour: String,
    cardExpiry: String,
    cardHolder: String,
    
    // Cho ATM:
    atmBankCode: String,
    atmBankName: String,
    atmAccountNumber: String,
    
    // Cho COD:
    deliveryAddress: String
  },
  description: String,      // Mô tả
  notes: String,            // Ghi chú
  createdAt: Date,
  updatedAt: Date
}
```

---

## ✅ Trạng Thái Thanh Toán

| Status | Ý Nghĩa | Khi Nào |
|--------|---------|---------|
| `pending` | Chờ xử lý | Sau khi tạo thanh toán |
| `processing` | Đang xử lý | Khi gửi thông tin thanh toán |
| `completed` | Hoàn thành | Thanh toán thành công |
| `failed` | Thất bại | Lỗi trong quá trình xử lý |
| `cancelled` | Bị hủy | Khách hàng yêu cầu hủy |

---

## 🔐 Bảo Mật

⚠️ **LƯU Ý:** Đây là demo. Trong production:
- ✅ Dùng HTTPS
- ✅ Hash/Encrypt số thẻ
- ✅ Dùng payment gateway thực (Stripe, VNPay, etc.)
- ✅ Validate card số thẻ (Luhn algorithm)
- ✅ Không lưu trữ CVV
- ✅ Thêm authentication & authorization

---

## 💾 MongoDB Connection

```bash
# Local MongoDB
mongodb://localhost:27017/BanGiay_App

# Remote MongoDB
mongodb+srv://user:password@cluster.mongodb.net/BanGiay_App
```

---

## 🚀 Chạy Server

```bash
npm install
npm start
```

Server sẽ chạy tại: `http://localhost:3000`

---

## 📝 Ví Dụ Flow Hoàn Chỉnh

1. **Bước 1**: Người dùng bấm [MUA NGAY] ở trang sản phẩm
2. **Bước 2**: App chuyển đến trang quá lộc, gọi API tạo Order
3. **Bước 3**: Người dùng chọn phương thức thanh toán (3 nút)
4. **Bước 4**: App gọi `POST /api/payment/create-payment`
5. **Bước 5**: Nhận được `paymentId`
6. **Bước 6**: Người dùng bấm nút thanh toán
7. **Bước 7**: App gọi endpoint phù hợp:
   - Thẻ Tín Dụng → `POST /api/payment/process-credit-card`
   - Thẻ ATM → `POST /api/payment/process-atm`
   - COD → `POST /api/payment/process-cod`
8. **Bước 8**: Thanh toán hoàn thành, app gọi `PUT /api/payment/:paymentId/confirm`
9. **Bước 9**: Cập nhật trạng thái Order thành "confirmed"

---

## 🆘 Lỗi Thường Gặp

| Lỗi | Giải Pháp |
|-----|----------|
| 404 Not Found | Kiểm tra paymentId hoặc orderId |
| 400 Bad Request | Kiểm tra input data |
| 500 Server Error | Kiểm tra MongoDB connection |
| Payment not found | PaymentId sai hoặc không tồn tại |

---

## 📞 Support
- GitHub: [link]
- Email: support@bangiay.vn

