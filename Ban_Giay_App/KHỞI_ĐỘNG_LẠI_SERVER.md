# ⚠️ QUAN TRỌNG: Cần khởi động lại Server API

## Vấn đề hiện tại:
- App đang gọi `POST /api/order` nhưng nhận được lỗi **404 Not Found**
- Server trả về HTML thay vì JSON vì route chưa được load

## Nguyên nhân:
- Order routes đã được tạo và đăng ký trong `server.js`
- Nhưng server chưa được **khởi động lại** nên routes mới chưa được load

## Giải pháp:

### Bước 1: Dừng server hiện tại (nếu đang chạy)

Mở PowerShell hoặc Terminal và chạy:
```powershell
# Tìm process Node.js đang chạy server
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force

# Hoặc tìm process đang dùng port 3000
$port = 3000
Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | ForEach-Object {
    Stop-Process -Id $_.OwningProcess -Force
}
```

### Bước 2: Khởi động lại server

```powershell
cd E:\du_an_1\BanGiay_Api
node server.js
```

Bạn sẽ thấy output như:
```
Server đang chạy tại http://localhost:3000
Server có thể truy cập từ mạng local tại: http://192.168.0.100:3000
```

**Lưu ý:** Giữ cửa sổ này mở, đừng đóng!

### Bước 3: Kiểm tra routes đã load

Sau khi server khởi động, bạn sẽ thấy trong console khi có request:
```
2025-12-04T... - POST /api/order
```

Nếu thấy dòng này, routes đã được load thành công!

### Bước 4: Test lại app

1. Rebuild Android app (Build > Clean Project > Rebuild Project)
2. Chạy lại app trên emulator
3. Thử tạo đơn hàng từ payment screen

## Kiểm tra nhanh:

Mở browser trong emulator và truy cập:
```
http://192.168.1.6:3000/
```

Bạn sẽ thấy: "API BanGiay đang chạy..."

## Troubleshooting:

### Nếu vẫn bị 404 sau khi khởi động lại:

1. **Kiểm tra file routes có tồn tại:**
   ```powershell
   Test-Path E:\du_an_1\BanGiay_Api\routes\order.routes.js
   ```
   Phải trả về `True`

2. **Kiểm tra middleware có tồn tại:**
   ```powershell
   Test-Path E:\du_an_1\BanGiay_Api\middleware\auth.middleware.js
   ```
   Phải trả về `True`

3. **Kiểm tra controller có tồn tại:**
   ```powershell
   Test-Path E:\du_an_1\BanGiay_Api\controllers\order.controller.js
   ```
   Phải trả về `True`

4. **Kiểm tra server.js có import routes:**
   Mở `E:\du_an_1\BanGiay_Api\server.js` và xem dòng 48:
   ```javascript
   app.use("/api/order", require("./routes/order.routes"));
   ```
   Phải có dòng này!

5. **Kiểm tra lỗi trong console khi khởi động server:**
   Nếu có lỗi như "Cannot find module", cần sửa đường dẫn hoặc cài đặt dependencies

## Sau khi khởi động lại thành công:

✅ App sẽ có thể tạo đơn hàng thành công
✅ Lỗi JSON parsing sẽ biến mất (đã được sửa trong NetworkUtils)
✅ Toast message sẽ hiển thị rõ ràng hơn về lỗi

---

**Tóm lại:** Server cần được khởi động lại để load Order routes mới!

