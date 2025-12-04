# Quick Fix: Emulator không kết nối được Server

## Vấn đề
Emulator không thể truy cập `http://10.0.2.2:3000/`

## Giải pháp nhanh nhất

### Option 1: Dùng IP thực của máy tính (Khuyến nghị)

1. **IP máy tính hiện tại:** `192.168.1.6`

2. **Cập nhật Base URL:**
   - Mở file: `app/build.gradle`
   - Dòng 19, đổi từ:
     ```
     buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
     ```
   - Sang:
     ```
     buildConfigField "String", "API_BASE_URL", "\"http://192.168.1.6:3000/api/\""
     ```

3. **Network Security Config đã được cập nhật** (đã thêm IP 192.168.1.6)

4. **Rebuild app:**
   - Build → Clean Project
   - Build → Rebuild Project
   - Chạy lại app

5. **Test:**
   - Trong emulator, mở Browser
   - Truy cập: `http://192.168.1.6:3000/`
   - Nếu thấy "API BanGiay đang chạy..." → OK ✅

### Option 2: Sửa Firewall

1. **Tạo rule cho port 3000:**
   ```powershell
   # Chạy PowerShell với quyền Administrator
   New-NetFirewallRule -DisplayName "Port 3000 Allow" -Direction Inbound -Protocol TCP -LocalPort 3000 -Action Allow
   ```

2. **Restart emulator**

3. **Test lại `http://10.0.2.2:3000/`**

## Checklist

- [ ] Server đang chạy tại `http://localhost:3000` ✅
- [ ] Firewall rule cho port 3000 đã được tạo
- [ ] Network Security Config đã có IP 192.168.1.6
- [ ] Base URL đã được cập nhật (nếu dùng Option 1)
- [ ] App đã được rebuild
- [ ] Emulator đã được restart

## Test từng bước

1. **Test từ máy tính:**
   ```
   http://localhost:3000/ → OK ✅
   http://192.168.1.6:3000/ → OK ✅
   ```

2. **Test từ emulator Browser:**
   ```
   http://10.0.2.2:3000/ → Test
   http://192.168.1.6:3000/ → Test (nếu dùng Option 1)
   ```

3. **Test từ app:**
   - Chạy app
   - Thử đăng nhập/đăng ký
   - Nếu OK → Network OK ✅
   - Thử thanh toán

