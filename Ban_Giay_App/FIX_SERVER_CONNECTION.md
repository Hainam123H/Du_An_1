# Hướng dẫn sửa lỗi "Không thể kết nối máy chủ"

## Vấn đề
App hiển thị lỗi "Không thể kết nối máy chủ" khi thanh toán, không lưu được thông tin vào MongoDB.

## Nguyên nhân
Server API chưa chạy hoặc không thể truy cập được từ emulator.

## Cách kiểm tra và sửa

### Bước 1: Kiểm tra Server API có đang chạy không

#### Cách 1: Kiểm tra trong trình duyệt
1. Mở trình duyệt trên máy tính
2. Truy cập: `http://localhost:3000/api/`
3. Nếu thấy response hoặc không có lỗi "This site can't be reached" → Server đang chạy
4. Nếu thấy lỗi "This site can't be reached" → Server chưa chạy

#### Cách 2: Kiểm tra bằng PowerShell/CMD
```powershell
# Kiểm tra port 3000 có đang được sử dụng không
netstat -ano | findstr :3000

# Nếu có kết quả → Server đang chạy
# Nếu không có kết quả → Server chưa chạy
```

### Bước 2: Khởi động Server API

1. **Mở terminal/PowerShell** trong thư mục project API:
   ```
   cd E:\du_an_1\BanGiay_Api
   ```

2. **Kiểm tra file package.json** để xem lệnh start:
   ```json
   "scripts": {
     "start": "node server.js",
     "dev": "nodemon server.js"
   }
   ```

3. **Chạy server:**
   ```bash
   # Nếu có nodemon
   npm run dev
   
   # Hoặc chạy trực tiếp
   npm start
   
   # Hoặc nếu dùng node
   node server.js
   ```

4. **Đợi server khởi động** - bạn sẽ thấy message như:
   ```
   Server is running on port 3000
   MongoDB connected
   ```

### Bước 3: Kiểm tra MongoDB có đang chạy không

1. **Kiểm tra MongoDB Service:**
   - Mở **Services** (Win + R → `services.msc`)
   - Tìm service **MongoDB** hoặc **MongoDB Server**
   - Nếu status là "Stopped" → Click phải → **Start**

2. **Hoặc khởi động MongoDB thủ công:**
   ```bash
   # Đi đến thư mục MongoDB bin
   cd "C:\Program Files\MongoDB\Server\7.0\bin"
   
   # Chạy MongoDB
   mongod.exe
   ```

### Bước 4: Kiểm tra cấu hình IP

App đang cấu hình kết nối đến:
- **Emulator:** `http://10.0.2.2:3000/api/` (đây là localhost của máy tính từ emulator)
- **Thiết bị thật:** Cần đổi sang IP của máy tính (ví dụ: `http://192.168.1.100:3000/api/`)

### Bước 5: Test kết nối từ App

1. **Khởi động emulator**
2. **Mở Browser trong emulator:**
   - Mở Chrome trong emulator
   - Truy cập: `http://10.0.2.2:3000/api/`
   - Nếu thấy response → Kết nối OK
   - Nếu không → Kiểm tra lại network security config

### Bước 6: Kiểm tra Firewall

Firewall có thể chặn port 3000:

1. Mở **Windows Defender Firewall**
2. Click **Advanced settings**
3. Click **Inbound Rules** → **New Rule**
4. Chọn **Port** → Next
5. Chọn **TCP** và nhập **3000** → Next
6. Chọn **Allow the connection** → Next
7. Chọn tất cả profiles → Next
8. Đặt tên "Node.js Port 3000" → Finish

## Checklist nhanh

- [ ] Server API đang chạy tại `http://localhost:3000`
- [ ] MongoDB đang chạy
- [ ] App cấu hình đúng IP: `http://10.0.2.2:3000/api/` (cho emulator)
- [ ] Firewall không chặn port 3000
- [ ] Emulator có kết nối mạng
- [ ] Network security config cho phép cleartext HTTP

## Kiểm tra nhanh trong Code

App đang sử dụng Base URL:
```java
// File: app/build.gradle
buildConfigField "String", "API_BASE_URL", "\"http://10.0.2.2:3000/api/\""
```

**Lưu ý:**
- `10.0.2.2` = localhost từ emulator
- Nếu dùng thiết bị thật, cần đổi thành IP máy tính (ví dụ: `192.168.1.100`)

## Test API endpoints cần thiết

1. **Tạo đơn hàng:** `POST http://localhost:3000/api/order`
2. **Tạo thanh toán:** `POST http://localhost:3000/api/payment/create-payment`
3. **Xử lý thẻ tín dụng:** `POST http://localhost:3000/api/payment/process-credit-card`

Bạn có thể test bằng Postman hoặc curl.

## Nếu vẫn không được

1. **Kiểm tra log server:**
   - Xem console của server API có hiển thị request không
   - Nếu không có request → App không gửi được request
   - Nếu có request nhưng lỗi → Kiểm tra code server

2. **Kiểm tra log app:**
   - Mở **Logcat** trong Android Studio
   - Filter: "ApiClient" hoặc "NetworkUtils"
   - Xem error messages chi tiết

3. **Thử đổi IP:**
   - Nếu dùng emulator → Giữ `10.0.2.2`
   - Nếu dùng thiết bị thật → Cần IP máy tính (kiểm tra bằng `ipconfig`)

