# ⚠️ FIX NGAY - Khởi động lại Server!

## Vấn đề:
App đang gọi `POST /api/order` nhưng server trả về **404 Not Found**

## Nguyên nhân:
Server chưa được khởi động lại sau khi thêm Order routes

## Giải pháp NHANH:

### Bước 1: Mở PowerShell

### Bước 2: Chạy lệnh này:

```powershell
cd E:\du_an_1\BanGiay_Api
.\restart-server.ps1
```

Hoặc thủ công:
```powershell
# Dừng server cũ
Get-Process node -ErrorAction SilentlyContinue | Stop-Process -Force

# Khởi động lại
cd E:\du_an_1\BanGiay_Api
node server.js
```

### Bước 3: Rebuild Android App

- Build > Clean Project
- Build > Rebuild Project
- Chạy lại app

### Bước 4: Test lại

Thử tạo đơn hàng từ payment screen → Sẽ không còn lỗi 404!

---

**Đọc file `HƯỚNG_DẪN_SỬA_LỖI.md` để biết chi tiết hơn!**

