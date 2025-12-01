# 📱 HƯỚNG DẪN PAYMENT API

## 📋 Tổng Quan

API Payment hỗ trợ các chức năng:
- ✅ Tạo thanh toán mới
- ✅ Lấy danh sách thanh toán
- ✅ Lấy chi tiết thanh toán
- ✅ Cập nhật trạng thái thanh toán
- ✅ Xóa thanh toán
- ✅ Lấy thống kê thanh toán

---

## 🗂️ Cấu Trúc Model Payment

```javascript
{
  userId: ObjectId (tùy chọn),
  orderId: ObjectId (tùy chọn),
  fullName: String (bắt buộc),
  email: String (bắt buộc),
  phoneNumber: String (bắt buộc),
  method: String (credit_card | atm | cod | bank_transfer),
  amount: Number (bắt buộc),
  status: String (pending | completed | failed | cancelled),
  transactionId: String (unique),
  description: String (tùy chọn),
  paymentDetails: {
    cardLastFour: String,
    bankName: String,
    bankCode: String
  },
  timestamps: { createdAt, updatedAt }
}
```

---

## 🚀 API Endpoints

### 1️⃣ Tạo Thanh Toán Mới
**POST** `/api/payment`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A",
  "email": "nguyenvana@example.com",
  "phoneNumber": "0981234567",
  "method": "cod",
  "amount": 500000,
  "description": "Thanh toán đơn hàng giày",
  "userId": "64f1a2b3c4d5e6f7g8h9i0j1",
  "orderId": "64f1a2b3c4d5e6f7g8h9i0j2",
  "paymentDetails": {
    "cardLastFour": "1234"
  }
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tạo thanh toán thành công",
  "payment": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0981234567",
    "method": "cod",
    "amount": 500000,
    "status": "completed",
    "transactionId": "PAY_1701388800000_abc12def",
    "createdAt": "2024-12-01T10:00:00.000Z",
    "updatedAt": "2024-12-01T10:00:00.000Z"
  }
}
```

---

### 2️⃣ Lấy Danh Sách Thanh Toán
**GET** `/api/payment`

**Query Parameters:**
- `status` (tùy chọn): pending, completed, failed, cancelled
- `method` (tùy chọn): credit_card, atm, cod, bank_transfer
- `page` (mặc định: 1)
- `limit` (mặc định: 10)

**Example:**
```
GET /api/payment?status=completed&method=cod&page=1&limit=5
```

**Response:**
```json
{
  "success": true,
  "total": 25,
  "page": 1,
  "pages": 5,
  "payments": [
    {
      "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "phoneNumber": "0981234567",
      "method": "cod",
      "amount": 500000,
      "status": "completed",
      "transactionId": "PAY_1701388800000_abc12def",
      "createdAt": "2024-12-01T10:00:00.000Z",
      "updatedAt": "2024-12-01T10:00:00.000Z"
    }
  ]
}
```

---

### 3️⃣ Lấy Chi Tiết Thanh Toán
**GET** `/api/payment/:id`

**Example:**
```
GET /api/payment/64f1a2b3c4d5e6f7g8h9i0j3
```

**Response:**
```json
{
  "success": true,
  "payment": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "userId": {
      "_id": "64f1a2b3c4d5e6f7g8h9i0j1",
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "phoneNumber": "0981234567"
    },
    "orderId": {
      "_id": "64f1a2b3c4d5e6f7g8h9i0j2",
      "totalAmount": 500000
    },
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0981234567",
    "method": "cod",
    "amount": 500000,
    "status": "completed",
    "transactionId": "PAY_1701388800000_abc12def",
    "description": "Thanh toán đơn hàng giày",
    "createdAt": "2024-12-01T10:00:00.000Z",
    "updatedAt": "2024-12-01T10:00:00.000Z"
  }
}
```

---

### 4️⃣ Cập Nhật Trạng Thái Thanh Toán
**PUT** `/api/payment/:id`

**Request Body:**
```json
{
  "status": "completed"
}
```

**Status hợp lệ:** pending, completed, failed, cancelled

**Response:**
```json
{
  "success": true,
  "message": "Cập nhật trạng thái thanh toán thành công",
  "payment": {
    "_id": "64f1a2b3c4d5e6f7g8h9i0j3",
    "status": "completed",
    "updatedAt": "2024-12-01T10:05:00.000Z"
  }
}
```

---

### 5️⃣ Xóa Thanh Toán
**DELETE** `/api/payment/:id`

**Example:**
```
DELETE /api/payment/64f1a2b3c4d5e6f7g8h9i0j3
```

**Response:**
```json
{
  "success": true,
  "message": "Xóa thanh toán thành công"
}
```

---

### 6️⃣ Lấy Thống Kê Thanh Toán
**GET** `/api/payment/stats`

**Response:**
```json
{
  "success": true,
  "stats": {
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
      },
      {
        "_id": "failed",
        "count": 2,
        "totalAmount": 1000000
      },
      {
        "_id": "cancelled",
        "count": 1,
        "totalAmount": 500000
      }
    ],
    "byMethod": [
      {
        "_id": "cod",
        "count": 12,
        "totalAmount": 6000000
      },
      {
        "_id": "bank_transfer",
        "count": 5,
        "totalAmount": 2500000
      },
      {
        "_id": "credit_card",
        "count": 4,
        "totalAmount": 2000000
      }
    ],
    "totalRevenue": 7500000
  }
}
```

---

## 💾 MongoDB Collections

### Payment Collection
```json
{
  "_id": ObjectId,
  "userId": ObjectId,
  "orderId": ObjectId,
  "fullName": String,
  "email": String,
  "phoneNumber": String,
  "method": String,
  "amount": Number,
  "status": String,
  "transactionId": String,
  "description": String,
  "paymentDetails": {
    "cardLastFour": String,
    "bankName": String,
    "bankCode": String
  },
  "createdAt": Date,
  "updatedAt": Date
}
```

### Index
```javascript
// Tạo index cho performance
db.payments.createIndex({ "transactionId": 1 });
db.payments.createIndex({ "userId": 1 });
db.payments.createIndex({ "status": 1 });
db.payments.createIndex({ "method": 1 });
db.payments.createIndex({ "createdAt": -1 });
```

---

## 🧪 Cách Test API

### Cách 1: Sử dụng file test
```bash
npm install axios
node test-payment-api.js
```

### Cách 2: Sử dụng cURL
```bash
# Tạo thanh toán
curl -X POST http://localhost:3000/api/payment \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "phoneNumber": "0981234567",
    "method": "cod",
    "amount": 500000
  }'

# Lấy danh sách
curl http://localhost:3000/api/payment

# Lấy chi tiết
curl http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3

# Cập nhật status
curl -X PUT http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3 \
  -H "Content-Type: application/json" \
  -d '{"status": "completed"}'

# Xóa
curl -X DELETE http://localhost:3000/api/payment/64f1a2b3c4d5e6f7g8h9i0j3

# Lấy thống kê
curl http://localhost:3000/api/payment/stats
```

### Cách 3: Sử dụng Postman
1. Import collection Payment API
2. Set request method và URL
3. Add request body (JSON)
4. Send request

---

## ✅ Yêu Cầu Input

| Field | Type | Bắt Buộc | Ghi Chú |
|-------|------|---------|--------|
| fullName | String | ✅ | Tên người thanh toán |
| email | String | ✅ | Email hợp lệ |
| phoneNumber | String | ✅ | Số điện thoại |
| method | String | ✅ | cod, credit_card, atm, bank_transfer |
| amount | Number | ✅ | > 0 |
| userId | ObjectId | ❌ | Liên kết đến User |
| orderId | ObjectId | ❌ | Liên kết đến Order |
| description | String | ❌ | Mô tả thanh toán |
| paymentDetails | Object | ❌ | Chi tiết thêm |

---

## ⚠️ Error Handling

### Lỗi 400 - Bad Request
```json
{
  "success": false,
  "message": "fullName, email, phoneNumber, method và amount là bắt buộc"
}
```

### Lỗi 404 - Not Found
```json
{
  "success": false,
  "message": "Không tìm thấy thanh toán"
}
```

### Lỗi 500 - Server Error
```json
{
  "success": false,
  "message": "Không thể tạo thanh toán",
  "error": "Error message"
}
```

---

## 🔄 Workflow Thanh Toán

```
pending → completed
       ↓
       → failed
       ↓
       → cancelled
```

---

## 📝 Ghi Chú

- TransactionId được tạo tự động: `PAY_{timestamp}_{random}`
- Timestamps tự động được thêm vào (createdAt, updatedAt)
- Email được chuẩn hóa thành lowercase
- Amount phải > 0
- Status mặc định là "completed" khi tạo thanh toán

---

## 🚀 Tiếp Theo

- [ ] Thêm validation email
- [ ] Thêm middleware authentication
- [ ] Thêm payment gateway integration (Stripe, VNPay, etc.)
- [ ] Thêm webhook support
- [ ] Thêm email notification
