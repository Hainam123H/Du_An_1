const axios = require("axios");

const API_URL = "http://localhost:3000/api";

// ==================== TEST FLOW THANH TOÁN ====================
// Flow: Chọn sản phẩm → Mua ngay → Chọn phương thức thanh toán → Thanh toán

class PaymentFlowTest {
  constructor() {
    this.userId = "507f1f77bcf86cd799439011"; // Mock userId
    this.orderId = "507f191e810c19729de860ea"; // Mock orderId
    this.paymentId = null;
  }

  log(message, type = "info") {
    const colors = {
      info: "\x1b[36m",
      success: "\x1b[32m",
      error: "\x1b[31m",
      warn: "\x1b[33m",
      reset: "\x1b[0m",
    };
    const color = colors[type] || colors.info;
    console.log(`${color}${message}${colors.reset}`);
  }

  async test(name, fn) {
    try {
      this.log(`\n📝 ${name}...`, "info");
      await fn();
    } catch (error) {
      this.log(`❌ ${name} - ${error.message}`, "error");
      if (error.response?.data) {
        this.log(`Response: ${JSON.stringify(error.response.data, null, 2)}`);
      }
    }
  }

  async runAllTests() {
    console.log("\n");
    console.log(
      "╔════════════════════════════════════════════════════════════════╗"
    );
    console.log(
      "║            🛍️  PAYMENT FLOW TEST - MUA NGAY GIÀY 👟            ║"
    );
    console.log(
      "╚════════════════════════════════════════════════════════════════╝"
    );

    // ==================== FLOW 1: TẠO ĐƠN THANH TOÁN ====================
    await this.test("Step 1: TẠO ĐƠN THANH TOÁN", async () => {
      const response = await axios.post(`${API_URL}/payment/create-payment`, {
        orderId: this.orderId,
        userId: this.userId,
        fullName: "Nguyễn Văn A",
        email: "nguyenvana@example.com",
        phoneNumber: "0981234567",
        method: "cod", // Mặc định
        amount: 500000, // 500k VNĐ
      });

      this.paymentId = response.data.data.paymentId;
      this.log(
        `✅ Đơn thanh toán tạo thành công\n   Payment ID: ${this.paymentId}\n   Transaction ID: ${response.data.data.transactionId}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    // ==================== FLOW 2: THANH TOÁN BẰNG THẺ TÍN DỤNG ====================
    await this.test("Step 2a: THANH TOÁN BẰNG THẺ TÍN DỤNG", async () => {
      const response = await axios.post(
        `${API_URL}/payment/process-credit-card`,
        {
          paymentId: this.paymentId,
          cardNumber: "4532015112830366", // Test card number
          cardExpiry: "12/25",
          cvv: "123",
          cardHolder: "NGUYEN VAN A",
        }
      );

      this.log(
        `✅ Đang xử lý thanh toán bằng thẻ tín dụng\n   Status: ${response.data.data.status}\n   Thẻ tận cùng: ${response.data.data.method}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    // Chờ 3 giây để xử lý
    await new Promise((resolve) => setTimeout(resolve, 3000));

    // ==================== FLOW 3: KIỂM TRA CHI TIẾT THANH TOÁN ====================
    await this.test("Step 3: KIỂM TRA CHI TIẾT THANH TOÁN", async () => {
      const response = await axios.get(`${API_URL}/payment/${this.paymentId}`);

      this.log(
        `✅ Chi tiết thanh toán:\n   Status: ${response.data.data.status}\n   Phương thức: ${response.data.data.method}\n   Số tiền: ${response.data.data.amount}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    // ==================== FLOW 4: THANH TOÁN BẰNG THẺ ATM ====================
    // Tạo payment mới cho test ATM
    await this.test("Step 4a: TẠO ĐƠN THANH TOÁN MỚI CHO ATM", async () => {
      const response = await axios.post(`${API_URL}/payment/create-payment`, {
        orderId: "507f191e810c19729de860eb",
        userId: this.userId,
        fullName: "Trần Thị B",
        email: "tranthib@example.com",
        phoneNumber: "0912345678",
        method: "atm",
        amount: 750000,
      });

      this.paymentId = response.data.data.paymentId;
      this.log(
        `✅ Đơn thanh toán mới tạo thành công\n   Payment ID: ${this.paymentId}`,
        "success"
      );
    });

    await this.test("Step 4b: THANH TOÁN BẰNG THẺ ATM", async () => {
      const response = await axios.post(`${API_URL}/payment/process-atm`, {
        paymentId: this.paymentId,
        atmBankCode: "VIETCOMBANK",
        atmBankName: "Vietcombank",
        atmAccountNumber: "12345678901234",
      });

      this.log(
        `✅ Đang xử lý thanh toán bằng ATM\n   Status: ${response.data.data.status}\n   Ngân hàng: ${response.data.data.bankCode}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    // ==================== FLOW 5: THANH TOÁN KHI NHẬN HÀNG (COD) ====================
    await this.test("Step 5a: TẠO ĐƠN THANH TOÁN MỚI CHO COD", async () => {
      const response = await axios.post(`${API_URL}/payment/create-payment`, {
        orderId: "507f191e810c19729de860ec",
        userId: this.userId,
        fullName: "Lê Văn C",
        email: "levanc@example.com",
        phoneNumber: "0933445566",
        method: "cod",
        amount: 1200000,
      });

      this.paymentId = response.data.data.paymentId;
      this.log(
        `✅ Đơn thanh toán mới tạo thành công\n   Payment ID: ${this.paymentId}`,
        "success"
      );
    });

    await this.test(
      "Step 5b: THANH TOÁN KHI NHẬN HÀNG (COD)",
      async () => {
        const response = await axios.post(`${API_URL}/payment/process-cod`, {
          paymentId: this.paymentId,
          deliveryAddress: "123 Nguyễn Huệ, Q.1, TP.HCM",
          notes: "Giao hàng vào ngày hôm sau, trước 17h",
        });

        this.log(
          `✅ Đơn hàng sẽ thanh toán khi nhận\n   Status: ${response.data.data.status}\n   Địa chỉ: ${response.data.data.deliveryAddress}`,
          "success"
        );
        console.log(`   Response:`, JSON.stringify(response.data, null, 2));
      }
    );

    // ==================== FLOW 6: XÁC NHẬN THANH TOÁN ====================
    await this.test("Step 6: XÁC NHẬN THANH TOÁN", async () => {
      const response = await axios.put(
        `${API_URL}/payment/${this.paymentId}/confirm`
      );

      this.log(
        `✅ Thanh toán đã được xác nhận\n   Status: ${response.data.data.status}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    // ==================== FLOW 7: LẤY DANH SÁCH THANH TOÁN ====================
    await this.test("Step 7: LẤY DANH SÁCH THANH TOÁN", async () => {
      const response = await axios.get(
        `${API_URL}/payment?userId=${this.userId}&page=1&limit=5`
      );

      this.log(
        `✅ Danh sách thanh toán:\n   Tổng: ${response.data.data.total}\n   Trang: ${response.data.data.page}/${response.data.data.pages}`,
        "success"
      );
      console.log(
        `   Payments:`,
        JSON.stringify(response.data.data.payments, null, 2)
      );
    });

    // ==================== FLOW 8: THỐNG KÊ THANH TOÁN ====================
    await this.test("Step 8: THỐNG KÊ THANH TOÁN", async () => {
      const response = await axios.get(`${API_URL}/payment/stats/overview`);

      this.log(
        `✅ Thống kê thanh toán:\n   Theo trạng thái: ${JSON.stringify(response.data.data.byStatus)}\n   Theo phương thức: ${JSON.stringify(response.data.data.byMethod)}`,
        "success"
      );
      console.log(
        `   Stats:`,
        JSON.stringify(response.data.data, null, 2)
      );
    });

    // ==================== FLOW 9: HỦY THANH TOÁN ====================
    // Tạo payment mới để test hủy
    await this.test("Step 9a: TẠO ĐƠN THANH TOÁN MỚI ĐỂ HỦY", async () => {
      const response = await axios.post(`${API_URL}/payment/create-payment`, {
        orderId: "507f191e810c19729de860ed",
        userId: this.userId,
        fullName: "Phạm Văn D",
        email: "phamvand@example.com",
        phoneNumber: "0944556677",
        method: "credit_card",
        amount: 350000,
      });

      this.paymentId = response.data.data.paymentId;
      this.log(
        `✅ Đơn thanh toán mới tạo thành công\n   Payment ID: ${this.paymentId}`,
        "success"
      );
    });

    await this.test("Step 9b: HỦY THANH TOÁN", async () => {
      const response = await axios.put(
        `${API_URL}/payment/${this.paymentId}/cancel`,
        {
          reason: "Khách hàng yêu cầu hủy",
        }
      );

      this.log(
        `✅ Thanh toán đã bị hủy\n   Status: ${response.data.data.status}\n   Ghi chú: ${response.data.data.notes}`,
        "success"
      );
      console.log(`   Response:`, JSON.stringify(response.data, null, 2));
    });

    console.log("\n");
    console.log(
      "╔════════════════════════════════════════════════════════════════╗"
    );
    console.log(
      "║                 ✅ FLOW TEST HOÀN THÀNH ✅                    ║"
    );
    console.log(
      "╚════════════════════════════════════════════════════════════════╝"
    );
  }
}

// Chạy tests
const tester = new PaymentFlowTest();
tester.runAllTests().catch(console.error);
