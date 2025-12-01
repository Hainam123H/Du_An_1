# 🔍 HƯỚNG DẪN XEM DỮ LIỆU PAYMENT TRONG MONGODB COMPASS

## Bước 1: Xem trong MongoDB Compass

1. Mở **MongoDB Compass**
2. Ở sidebar bên trái, tìm **BanGiay_App** database
3. Expand BanGiay_App → Bạn sẽ thấy collections:
   - ❌ `products`
   - ❌ `users`
   - ✅ `payments` ← **Collection thanh toán (mới tạo)**

4. Click vào **`payments`** để xem dữ liệu

## Bước 2: Xem chi tiết thanh toán

Khi bạn click vào `payments`, bạn sẽ thấy:
- 📊 Documents: 9 (số lượng thanh toán)
- 💾 Storage size: ~10-15 kB
- 🔑 Indexes: 6

**Các field trong mỗi payment document:**
```
_id                  → ID duy nhất
orderId              → Liên kết đến Order
userId               → Liên kết đến User
fullName             → Tên khách hàng
email                → Email khách hàng
phoneNumber          → SĐT khách hàng
method               → Phương thức (cod, credit_card, atm)
amount               → Số tiền
status               → Trạng thái (completed, pending, processing, failed, cancelled)
transactionId        → ID giao dịch duy nhất
paymentDetails       → Chi tiết thanh toán
createdAt            → Thời gian tạo
updatedAt            → Thời gian cập nhật
```

## Bước 3: Tìm kiếm dữ liệu

### Tìm theo Status
```javascript
{ status: "completed" }
```
→ Sẽ hiển thị 5 thanh toán đã hoàn thành

### Tìm theo Phương thức
```javascript
{ method: "cod" }
```
→ Sẽ hiển thị 3 thanh toán COD

### Tìm theo Email
```javascript
{ email: "nguyenvana@example.com" }
```

### Tìm theo Số tiền
```javascript
{ amount: { $gte: 500000 } }
```
→ Thanh toán từ 500k VNĐ trở lên

## 📊 Dữ liệu Mẫu Được Tạo

| # | Tên | Email | Phương thức | Số tiền | Trạng thái |
|---|-----|-------|-----------|---------|-----------|
| 1 | Nguyễn Văn A | nguyenvana@example.com | COD | 500.000 | ✅ Completed |
| 2 | Trần Thị B | tranthib@example.com | Credit Card | 750.000 | ✅ Completed |
| 3 | Lê Văn C | levanc@example.com | ATM | 1.200.000 | ✅ Completed |
| 4 | Phạm Văn D | phamvand@example.com | COD | 350.000 | ⏳ Pending |
| 5 | Hoàng Thị E | hoangthie@example.com | Credit Card | 650.000 | ✅ Completed |
| 6 | Võ Văn F | vovanf@example.com | ATM | 900.000 | ✅ Completed |
| 7 | Dương Thị G | duongthig@example.com | COD | 420.000 | ❌ Failed |
| 8 | Bùi Văn H | buivanh@example.com | Credit Card | 1.100.000 | 🚫 Cancelled |
| 9 | Cao Thị I | caothii@example.com | ATM | 580.000 | 🔄 Processing |

**Tổng:** 6.450.000 VNĐ

## 🔗 Xem qua API

### Lấy tất cả thanh toán
```bash
curl http://localhost:3000/api/payment
```

### Lấy chi tiết 1 thanh toán
```bash
curl http://localhost:3000/api/payment/{paymentId}
```

### Lọc theo status
```bash
curl "http://localhost:3000/api/payment?status=completed"
```

### Lọc theo phương thức
```bash
curl "http://localhost:3000/api/payment?method=cod"
```

### Lấy thống kê
```bash
curl http://localhost:3000/api/payment/stats/overview
```

## 🖥️ Xem bằng MongoDB Shell

Mở **MongoDB Shell** trong Compass:
1. Click **"Open MongoDB shell"** button
2. Gõ lệnh:

```javascript
// Xem tất cả
db.payments.find()

// Xem theo status
db.payments.find({ status: "completed" })

// Đếm theo phương thức
db.payments.aggregate([
  { $group: { _id: "$method", count: { $sum: 1 } } }
])

// Thống kê tổng tiền
db.payments.aggregate([
  { $match: { status: "completed" } },
  { $group: { _id: null, total: { $sum: "$amount" } } }
])

// Lấy top 5 thanh toán cao nhất
db.payments.find().sort({ amount: -1 }).limit(5)
```

## 📋 Collection Structure

```javascript
db.createCollection("payments", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["orderId", "userId", "fullName", "email", "phoneNumber", "method", "amount"],
      properties: {
        _id: { bsonType: "objectId" },
        orderId: { bsonType: "objectId" },
        userId: { bsonType: "objectId" },
        fullName: { bsonType: "string" },
        email: { bsonType: "string" },
        phoneNumber: { bsonType: "string" },
        method: { enum: ["cod", "credit_card", "atm"] },
        amount: { bsonType: "number" },
        status: { enum: ["pending", "processing", "completed", "failed", "cancelled"] },
        transactionId: { bsonType: "string" },
        paymentDetails: { bsonType: "object" },
        createdAt: { bsonType: "date" },
        updatedAt: { bsonType: "date" }
      }
    }
  }
})
```

## 🔍 Indexes

Danh sách index đã tạo:
1. `orderId: 1` - Tìm nhanh thanh toán theo order
2. `userId: 1` - Tìm nhanh thanh toán theo user
3. `status: 1` - Lọc theo trạng thái
4. `method: 1` - Lọc theo phương thức
5. `createdAt: -1` - Sắp xếp theo thời gian
6. `transactionId: 1` (unique) - ID giao dịch duy nhất

## 💡 Mẹo

- **Refresh data**: Click nút **Refresh** (⟲) ở top right
- **Thay đổi view**: Có 3 chế độ xem: List, JSON, Table
- **Export dữ liệu**: Click **EXPORT DATA** để xuất CSV/JSON
- **Thêm dữ liệu**: Click **ADD DATA** để thêm document mới
- **Tìm kiếm**: Sử dụng filter box để tìm kiếm nhanh

