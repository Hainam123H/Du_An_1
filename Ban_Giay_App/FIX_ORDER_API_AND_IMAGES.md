# Khắc phục lỗi Order API và Image URLs

## ✅ Đã khắc phục: Lỗi 404 cho `/api/order` endpoint

### Vấn đề:
- Android app gọi `POST /api/order` nhưng server không có route này
- Logcat hiển thị: `404 Not Found http://192.168.1.6:3000/api/order`

### Giải pháp đã thực hiện:
1. **Tạo JWT Middleware** (`BanGiay_Api/middleware/auth.middleware.js`)
   - Xác thực token từ Authorization header
   - Lấy user từ token để sử dụng trong các controller

2. **Tạo Order Controller** (`BanGiay_Api/controllers/order.controller.js`)
   - `createOrder`: Tạo đơn hàng mới
   - `getMyOrders`: Lấy danh sách đơn hàng của user
   - `getOrderById`: Lấy chi tiết đơn hàng
   - `cancelOrder`: Hủy đơn hàng
   - Mapping giữa database format và Android app format

3. **Tạo Order Routes** (`BanGiay_Api/routes/order.routes.js`)
   - Tất cả routes đều yêu cầu xác thực (verifyToken middleware)
   - `POST /api/order` - Tạo đơn hàng
   - `GET /api/order` - Lấy danh sách đơn hàng
   - `GET /api/order/:id` - Lấy chi tiết đơn hàng
   - `PUT /api/order/:id/cancel` - Hủy đơn hàng

4. **Đăng ký routes trong server.js**
   - Thêm `app.use("/api/order", require("./routes/order.routes"));`

### Các bước tiếp theo:
1. **Khởi động lại server API:**
   ```powershell
   cd E:\du_an_1\BanGiay_Api
   node server.js
   ```

2. **Rebuild Android app:**
   - Build > Clean Project
   - Build > Rebuild Project
   - Chạy lại app trên emulator

3. **Test Order API:**
   - Thử tạo đơn hàng từ payment screen
   - Kiểm tra Logcat xem có còn lỗi 404 không

---

## ⚠️ Vấn đề cần xử lý: Image URLs từ `example.com`

### Vấn đề:
- Logcat hiển thị lỗi Glide khi tải ảnh:
  ```
  Load failed for [https://example.com/images/converse-chuck-taylor.jpg] with dimensions [31x47]
  Caused by: java.io.FileNotFoundException: https://example.com/images/converse-chuck-taylor.jpg
  com.bumptech.glide.load.HttpException(Failed to connect or obtain data, status code: 404)
  ```

- Database đang lưu URL ảnh là `https://example.com/images/...` (placeholder, không tồn tại)

### Giải pháp (chọn 1 trong 2):

#### **Giải pháp 1: Sử dụng ảnh từ Android Drawable Resources**

1. Đảm bảo ảnh đã có trong `app/src/main/res/drawable/` (ví dụ: `giay15.jpg`, `giay14.jpg`, ...)

2. Cập nhật database để lưu tên file (không có URL):
   ```javascript
   // Chạy script update-product-images.js
   cd E:\du_an_1\BanGiay_Api
   node update-product-images.js
   ```
   - Đảm bảo `USE_ANDROID_RESOURCE = true` trong file này

3. Sửa code Android để load ảnh từ drawable nếu URL là tên file đơn giản:
   ```java
   // Trong ProductAdapter hoặc nơi load ảnh
   if (imageUrl.startsWith("http")) {
       // Load từ URL
       Glide.with(context).load(imageUrl).into(imageView);
   } else {
       // Load từ drawable resource
       int resId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
       Glide.with(context).load(resId).into(imageView);
   }
   ```

#### **Giải pháp 2: Host ảnh trên server**

1. Tạo thư mục `images/` trong `BanGiay_Api/` và đặt các file ảnh vào đó

2. Thêm route static files trong `server.js`:
   ```javascript
   const path = require("path");
   app.use("/images", express.static(path.join(__dirname, "images")));
   ```

3. Cập nhật database với URL đúng:
   ```javascript
   // Cập nhật BASE_IMAGE_URL trong update-product-images.js
   const BASE_IMAGE_URL = "http://192.168.1.6:3000/images/";
   const USE_ANDROID_RESOURCE = false;
   
   // Chạy script
   node update-product-images.js
   ```

4. Hoặc cập nhật trực tiếp trong MongoDB:
   ```javascript
   // Kết nối MongoDB và cập nhật
   db.products.updateMany(
     { hinh_anh: { $regex: "example.com" } },
     [
       {
         $set: {
           hinh_anh: {
             $replaceAll: {
               input: "$hinh_anh",
               find: "https://example.com/images/",
               replacement: "http://192.168.1.6:3000/images/"
             }
           }
         }
       }
     ]
   )
   ```

### Khuyến nghị:
- **Development**: Dùng Giải pháp 1 (drawable resources) - nhanh và không cần server
- **Production**: Dùng Giải pháp 2 (host trên server) - linh hoạt và dễ quản lý

---

## 📝 Tóm tắt các file đã tạo/sửa:

1. ✅ `BanGiay_Api/middleware/auth.middleware.js` - JWT authentication middleware
2. ✅ `BanGiay_Api/controllers/order.controller.js` - Order CRUD operations
3. ✅ `BanGiay_Api/routes/order.routes.js` - Order API routes
4. ✅ `BanGiay_Api/server.js` - Đã thêm order routes

## 🔍 Kiểm tra sau khi sửa:

1. Server API đang chạy: `http://192.168.1.6:3000`
2. Test Order API bằng Postman hoặc từ app:
   - `POST http://192.168.1.6:3000/api/order` (cần token)
3. Kiểm tra Logcat không còn lỗi 404 cho `/api/order`
4. Xử lý image URLs theo một trong hai giải pháp trên

