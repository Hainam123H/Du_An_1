# Hướng dẫn sửa lỗi Timeout 3000000 millis

## Lỗi: "Command has been inactive for more than 3000000 millis"

### Nguyên nhân:
- Emulator đang bị treo hoặc không phản hồi
- Build process bị kẹt
- Android Studio đang chờ một process nào đó

### Giải pháp:

#### Bước 1: Dừng TẤT CẢ processes đang chạy
1. Trong Android Studio:
   - Click vào **View → Tool Windows → Build** để xem build output
   - Click nút **Stop** (hình vuông đỏ) nếu có process đang chạy
   - Hoặc nhấn `Ctrl + F2` để stop

2. Dừng emulator:
   - Mở **Device Manager** (bên phải)
   - Nếu có emulator đang chạy (có nút Stop), click **Stop** để tắt
   - Đợi vài giây

#### Bước 2: Restart Android Studio
1. Vào **File → Exit** để đóng Android Studio hoàn toàn
2. Mở lại Android Studio
3. Mở project lại

#### Bước 3: Invalidate Caches
1. Vào **File → Invalidate Caches...**
2. Chọn **Invalidate and Restart**
3. Đợi Android Studio restart (2-3 phút)

#### Bước 4: Clean & Rebuild
1. **Build → Clean Project** (đợi xong)
2. **Build → Rebuild Project** (đợi xong, có thể mất 3-5 phút)

#### Bước 5: Khởi động emulator TRƯỚC
1. Mở **Device Manager**
2. Click nút **Play** (mũi tên xanh) bên cạnh Pixel 7 hoặc Pixel 9
3. **Đợi emulator khởi động HOÀN TOÀN** (thấy màn hình home Android)
4. Đợi thêm 10-20 giây để emulator ổn định

#### Bước 6: Chạy app
1. Đảm bảo emulator đã sẵn sàng (không còn "Activating")
2. Nhấn nút **Run** (mũi tên xanh) hoặc `Shift + F10`
3. Đợi app cài đặt và chạy

### Nếu vẫn bị lỗi:

#### Giải pháp nâng cao:
1. **Kiểm tra Android Studio Settings:**
   - File → Settings → Build, Execution, Deployment → Build Tools → Gradle
   - Đảm bảo "Gradle JDK" đã được chọn
   - Tắt "Build and run using" nếu đang bật

2. **Tăng timeout trong Android Studio:**
   - File → Settings → Build, Execution, Deployment → Compiler
   - Tìm "Build process heap size" và tăng lên 2048 hoặc 4096
   - Tìm "Shared build process VM options" và thêm: `-Xmx2048m`

3. **Kiểm tra emulator:**
   - Nếu emulator chạy chậm, thử tạo emulator mới với cấu hình thấp hơn
   - Hoặc dùng thiết bị thật qua USB

### Lưu ý quan trọng:
- **KHÔNG** chạy app khi emulator đang "Activating"
- **LUÔN** đợi emulator khởi động xong trước khi run
- Nếu build quá lâu (>10 phút), cancel và thử lại
- Đảm bảo server API đang chạy tại `http://10.0.2.2:3000/api/`

