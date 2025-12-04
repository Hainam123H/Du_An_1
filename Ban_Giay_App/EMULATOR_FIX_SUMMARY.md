# Tóm tắt: Sửa lỗi Emulator không kết nối Server

## Đã thực hiện

### ✅ 1. Đã cập nhật Base URL
- File: `app/build.gradle`
- Đổi từ: `http://10.0.2.2:3000/api/`
- Sang: `http://192.168.1.6:3000/api/` (IP thực của máy tính)

### ✅ 2. Đã cập nhật Network Security Config
- File: `app/src/main/res/xml/network_security_config.xml`
- Đã thêm domain: `192.168.1.6`

### ✅ 3. Server đang chạy
- Port: 3000
- Listen trên: `0.0.0.0:3000` (có thể truy cập từ mạng)
- Status: ✅ Đang hoạt động

## Bước tiếp theo

### Bước 1: Rebuild App
1. Trong Android Studio:
   - **Build → Clean Project**
   - **Build → Rebuild Project**
   - Đợi build xong (2-3 phút)

### Bước 2: Restart Emulator
1. Tắt emulator hiện tại
2. Khởi động lại emulator
3. Đợi emulator khởi động xong

### Bước 3: Test kết nối
1. **Test từ Browser trong Emulator:**
   - Mở Chrome trong emulator
   - Truy cập: `http://192.168.1.6:3000/`
   - Nếu thấy "API BanGiay đang chạy..." → ✅ OK

2. **Test từ App:**
   - Chạy app trên emulator
   - Thử đăng nhập/đăng ký
   - Nếu OK → Network hoạt động ✅
   - Thử thanh toán lại

## Nếu vẫn không được

### Kiểm tra Firewall
```powershell
# Chạy PowerShell với quyền Administrator
New-NetFirewallRule -DisplayName "Port 3000 Allow" -Direction Inbound -Protocol TCP -LocalPort 3000 -Action Allow
```

### Kiểm tra Server Logs
- Xem console của server API
- Xem có request nào đến không khi test từ app

### Kiểm tra Logcat
- Mở Logcat trong Android Studio
- Filter: "OkHttp" hoặc "ApiClient"
- Xem error messages chi tiết

## Lưu ý

- **IP máy tính:** `192.168.1.6`
- Nếu IP thay đổi (khi đổi mạng WiFi), cần cập nhật lại Base URL
- Server phải luôn chạy khi test app
- MongoDB phải đang chạy

## Nếu muốn quay lại dùng 10.0.2.2

1. Sửa lại `app/build.gradle`:
   ```
   buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
   ```

2. Tạo firewall rule cho port 3000

3. Rebuild app

