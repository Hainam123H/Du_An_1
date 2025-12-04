# Hướng dẫn sửa lỗi 404 cho Order API

## ❌ Lỗi hiện tại:

1. **404 Not Found** khi gọi `POST /api/order`
2. **JSON parsing error** vì server trả về HTML thay vì JSON

## ✅ Đã sửa:

1. ✅ Tạo Order API routes, controller, middleware
2. ✅ Đăng ký routes vào server.js
3. ✅ Sửa NetworkUtils để xử lý lỗi tốt hơn (khi server trả về HTML)

## 🔧 Cần làm NGAY:

### **Bước 1: Khởi động lại Server API**

Server cần được khởi động lại để load routes mới. Có 2 cách:

#### **Cách 1: Dùng script tự động (KHUYẾN NGHỊ)**

Mở PowerShell trong thư mục `E:\du_an_1\BanGiay_Api` và chạy:
```powershell
.\restart-server.ps1
```

Script này sẽ:
- Tự động dừng server cũ
- Kiểm tra các file cần thiết
- Khởi động server mới

#### **Cách 2: Thủ công**

1. **Dừng server cũ:**
   - Tìm cửa sổ terminal/console đang chạy `node server.js`
   - Nhấn `Ctrl+C` để dừng
   - Hoặc dùng PowerShell:
     ```powershell
     Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force
     ```

2. **Khởi động lại server:**
   ```powershell
   cd E:\du_an_1\BanGiay_Api
   node server.js
   ```

3. **Kiểm tra server đã chạy:**
   Bạn sẽ thấy trong console:
   ```
   Server đang chạy tại http://localhost:3000
   Server có thể truy cập từ mạng local tại: http://192.168.0.100:3000
   ```

### **Bước 2: Rebuild Android App**

1. Mở Android Studio
2. Build > Clean Project
3. Build > Rebuild Project
4. Chạy lại app trên emulator

### **Bước 3: Test lại**

1. Mở app
2. Đăng nhập (nếu chưa)
3. Chọn sản phẩm và mua
4. Điền thông tin thanh toán
5. Thử tạo đơn hàng

## 🔍 Kiểm tra nhanh:

### Test từ browser trong emulator:

1. Mở browser trong emulator
2. Truy cập: `http://192.168.1.6:3000/`
3. Phải thấy: "API BanGiay đang chạy..."

### Test từ máy tính:

Mở browser và truy cập:
- `http://localhost:3000/` → "API BanGiay đang chạy..."
- `http://192.168.1.6:3000/` → "API BanGiay đang chạy..."

## ⚠️ Lưu ý:

1. **Server phải được khởi động lại** sau khi thêm/sửa routes
2. **Giữ cửa sổ terminal chạy server mở** - đừng đóng!
3. **Nếu có lỗi khi khởi động server**, kiểm tra:
   - MongoDB đang chạy
   - Port 3000 chưa bị chiếm bởi process khác
   - Tất cả dependencies đã được cài đặt (`npm install`)

## 📋 Tóm tắt các file đã tạo:

1. ✅ `BanGiay_Api/middleware/auth.middleware.js` - JWT authentication
2. ✅ `BanGiay_Api/controllers/order.controller.js` - Order operations
3. ✅ `BanGiay_Api/routes/order.routes.js` - Order routes
4. ✅ `BanGiay_Api/server.js` - Đã thêm order routes
5. ✅ `BanGiay_Api/restart-server.ps1` - Script khởi động lại server

## 🎯 Sau khi khởi động lại server:

✅ Lỗi 404 sẽ biến mất
✅ App có thể tạo đơn hàng thành công
✅ Error messages sẽ rõ ràng hơn (đã sửa trong NetworkUtils)

---

**QUAN TRỌNG NHẤT:** Khởi động lại server API ngay bây giờ!

