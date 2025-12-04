# Hướng dẫn sửa lỗi Emulator không kết nối được Server

## Vấn đề
Emulator không thể truy cập `http://10.0.2.2:3000/` từ browser hoặc app.

## Kiểm tra nhanh

### Server Status ✅
- Server đang chạy trên port 3000
- Server đang listen trên `0.0.0.0:3000` (có thể truy cập từ mạng)
- IP máy tính: `192.168.1.6`
- Server có thể truy cập từ máy tính: ✅

### Vấn đề: Emulator không kết nối được

## Giải pháp

### Giải pháp 1: Kiểm tra Firewall (Quan trọng!)

1. **Mở Windows Defender Firewall:**
   - Nhấn `Win + R`
   - Gõ: `wf.msc`
   - Enter

2. **Kiểm tra Inbound Rules:**
   - Click **Inbound Rules** ở bên trái
   - Tìm rule cho port 3000
   - Nếu không có → Cần tạo rule mới

3. **Tạo Firewall Rule mới:**
   ```powershell
   # Chạy PowerShell với quyền Administrator
   New-NetFirewallRule -DisplayName "Node.js Port 3000" -Direction Inbound -Protocol TCP -LocalPort 3000 -Action Allow
   ```

### Giải pháp 2: Kiểm tra Emulator Network

1. **Kiểm tra Emulator có internet không:**
   - Mở Browser trong emulator
   - Truy cập: `https://www.google.com`
   - Nếu không vào được → Emulator không có internet

2. **Restart Emulator:**
   - Tắt emulator hoàn toàn
   - Khởi động lại emulator
   - Đợi emulator khởi động xong

3. **Kiểm tra DNS:**
   - Trong emulator, Settings → Network
   - Xem DNS settings

### Giải pháp 3: Thử dùng IP thực của máy tính

Nếu `10.0.2.2` không hoạt động, thử dùng IP thực của máy tính:

1. **IP máy tính hiện tại:** `192.168.1.6`

2. **Cập nhật Base URL trong app:**
   - File: `app/build.gradle`
   - Đổi từ: `http://10.0.2.2:3000/api/`
   - Sang: `http://192.168.1.6:3000/api/`

3. **Cập nhật Network Security Config:**
   - File: `app/src/main/res/xml/network_security_config.xml`
   - Thêm domain: `192.168.1.6`

4. **Rebuild app:**
   - Clean Project
   - Rebuild Project
   - Chạy lại app

### Giải pháp 4: Kiểm tra Server đang listen đúng

1. **Kiểm tra server đang listen trên interface nào:**
   ```powershell
   netstat -ano | findstr :3000
   ```
   
   Phải thấy: `TCP    0.0.0.0:3000` (đang listen trên tất cả interfaces)

2. **Nếu chỉ thấy `127.0.0.1:3000`:**
   - Server chỉ listen trên localhost
   - Cần sửa server để listen trên `0.0.0.0`

### Giải pháp 5: Test từng bước

1. **Test từ máy tính:**
   - Browser: `http://localhost:3000/` → Phải thấy "API BanGiay đang chạy..."
   - Browser: `http://192.168.1.6:3000/` → Phải thấy "API BanGiay đang chạy..."

2. **Test từ emulator:**
   - Browser trong emulator: `http://10.0.2.2:3000/`
   - Nếu không được → Vấn đề network
   - Nếu được → Vấn đề ở app

3. **Test từ thiết bị thật (nếu có):**
   - Dùng IP: `http://192.168.1.6:3000/`
   - Cả máy và thiết bị phải cùng mạng WiFi

## Debug Steps

### Bước 1: Kiểm tra Firewall
```powershell
# Kiểm tra có rule nào cho port 3000 không
Get-NetFirewallRule | Where-Object {$_.DisplayName -like "*3000*"}

# Nếu không có, tạo rule mới
New-NetFirewallRule -DisplayName "Node.js Port 3000" -Direction Inbound -Protocol TCP -LocalPort 3000 -Action Allow
```

### Bước 2: Kiểm tra Server
```powershell
# Test server từ máy tính
Invoke-WebRequest -Uri "http://localhost:3000/" -UseBasicParsing
Invoke-WebRequest -Uri "http://192.168.1.6:3000/" -UseBasicParsing

# Kiểm tra server đang listen
netstat -ano | findstr :3000
```

### Bước 3: Kiểm tra Emulator
1. Mở Browser trong emulator
2. Truy cập: `http://10.0.2.2:3000/`
3. Nếu lỗi → Vấn đề network/firewall
4. Nếu OK → Vấn đề ở app config

### Bước 4: Kiểm tra Logcat
1. Mở Logcat trong Android Studio
2. Filter: "OkHttp" hoặc "ApiClient"
3. Thử thanh toán lại
4. Xem error messages

## Quick Fix

Nếu muốn nhanh, thử đổi sang dùng IP thực:

1. **Sửa build.gradle:**
   ```
   buildConfigField "String", "API_BASE_URL", "\"http://192.168.1.6:3000/api/\""
   ```

2. **Sửa network_security_config.xml:**
   - Thêm domain: `192.168.1.6`

3. **Rebuild và test lại**

