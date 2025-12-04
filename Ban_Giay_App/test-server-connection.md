# Test kết nối Server từ Emulator

## Server đang chạy ✅

- **Port:** 3000
- **Process ID:** 41592
- **Status:** Đang hoạt động
- **Test endpoint:** `/api/product` - ✅ Trả về dữ liệu thành công

## Vấn đề: Emulator không kết nối được

### Cách test từ Emulator:

1. **Mở Browser trong Emulator:**
   - Mở Chrome trong emulator (Pixel 7 hoặc Pixel 9)
   - Truy cập: `http://10.0.2.2:3000/`
   - Nếu thấy "API BanGiay đang chạy..." → Kết nối OK ✅
   - Nếu không → Có vấn đề với network config ❌

2. **Test API endpoint:**
   - Truy cập: `http://10.0.2.2:3000/api/product`
   - Nếu thấy JSON data → Kết nối OK ✅
   - Nếu lỗi → Kiểm tra lại

### Kiểm tra cấu hình:

1. **Network Security Config:**
   - File: `app/src/main/res/xml/network_security_config.xml`
   - Đã cấu hình cho phép cleartext HTTP với `10.0.2.2` ✅

2. **Base URL trong App:**
   - File: `app/build.gradle`
   - `API_BASE_URL = "http://10.0.2.2:3000/api/"` ✅

### Debug steps:

1. **Kiểm tra Logcat:**
   - Mở Logcat trong Android Studio
   - Filter: "ApiClient" hoặc "OkHttp"
   - Xem error messages khi thanh toán

2. **Kiểm tra Server Logs:**
   - Xem console của server API
   - Xem có request nào đến không
   - Nếu không có request → App không gửi được request

3. **Test từ App:**
   - Thử đăng nhập/đăng ký trước
   - Nếu OK → Network OK, vấn đề ở payment API
   - Nếu lỗi → Vấn đề network config

## Giải pháp nếu vẫn lỗi:

### 1. Kiểm tra Firewall:
```powershell
# Cho phép port 3000 qua firewall
New-NetFirewallRule -DisplayName "Node.js Port 3000" -Direction Inbound -Protocol TCP -LocalPort 3000 -Action Allow
```

### 2. Đổi IP nếu cần:
- Nếu dùng thiết bị thật thay vì emulator
- Cần đổi Base URL thành IP máy tính
- Tìm IP: `ipconfig` → IPv4 Address

### 3. Kiểm tra MongoDB:
- Đảm bảo MongoDB đang chạy
- Service: MongoDB Server

### 4. Restart server:
```bash
cd E:\du_an_1\BanGiay_Api
npm start
```

