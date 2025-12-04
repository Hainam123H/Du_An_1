# QUAN TRỌNG: Cần REBUILD App sau khi đổi Base URL!

## Vấn đề
App vẫn hiển thị lỗi "Không thể kết nối máy chủ" mặc dù đã đổi Base URL.

## Nguyên nhân
**BuildConfig.API_BASE_URL được tạo khi BUILD app!**

Nếu chỉ sửa file `build.gradle` mà không rebuild, app vẫn dùng Base URL cũ từ lần build trước!

## Giải pháp BẮT BUỘC

### Bước 1: Clean Project
1. Trong Android Studio
2. **Build → Clean Project**
3. Đợi clean xong

### Bước 2: Rebuild Project  
1. **Build → Rebuild Project**
2. Hoặc nhấn `Ctrl + F9`
3. Đợi build xong (2-5 phút)

### Bước 3: Uninstall App cũ
1. Trong emulator: Settings → Apps → Tìm app "Ban Giay App"
2. Click "Uninstall"
3. Hoặc từ Android Studio: Device Manager → Click vào device → Uninstall app

### Bước 4: Install lại App
1. Chạy lại app từ Android Studio (nút Run)
2. App sẽ được build và install lại với Base URL mới

### Bước 5: Kiểm tra
1. Kiểm tra Logcat để xem Base URL mới:
   - Filter: "ApiClient" hoặc "Base URL"
   - Hoặc thêm log trong code để in ra Base URL đang dùng

## Kiểm tra Base URL đang dùng

Thêm log tạm thời trong `ApiClient.java`:

```java
private static Retrofit buildRetrofit() {
    // Log Base URL để kiểm tra
    android.util.Log.d("ApiClient", "Base URL: " + BuildConfig.API_BASE_URL);
    
    // ... rest of code
}
```

Sau đó check Logcat để xem Base URL thực sự đang dùng.

## Checklist

- [ ] Đã sửa `app/build.gradle` - Base URL = `http://192.168.1.6:3000/api/`
- [ ] Đã Clean Project
- [ ] Đã Rebuild Project  
- [ ] Đã Uninstall app cũ
- [ ] Đã Install lại app mới
- [ ] Server đang chạy tại `http://192.168.1.6:3000`
- [ ] Test từ browser trong emulator: `http://192.168.1.6:3000/` → OK

## Nếu vẫn lỗi sau khi rebuild

1. **Kiểm tra Logcat:**
   - Xem error message chi tiết
   - Filter: "OkHttp" hoặc "ApiClient"

2. **Kiểm tra Server Logs:**
   - Xem console của server
   - Xem có request nào đến không

3. **Test endpoint cụ thể:**
   - Từ browser trong emulator: `http://192.168.1.6:3000/api/product`
   - Phải thấy JSON data

## Lưu ý

- **MỖI LẦN đổi Base URL đều phải REBUILD!**
- BuildConfig chỉ được tạo khi build
- App đã install sẽ dùng BuildConfig cũ nếu không rebuild

