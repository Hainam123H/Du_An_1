const axios = require("axios");

const BASE_URL = "http://localhost:3000/api/payment";

// Màu cho console
const colors = {
  reset: "\x1b[0m",
  green: "\x1b[32m",
  red: "\x1b[31m",
  yellow: "\x1b[33m",
  blue: "\x1b[34m",
};

const log = (message, color = "reset") => {
  console.log(`${colors[color]}${message}${colors.reset}`);
};

// Test dữ liệu
const testPayment = {
  fullName: "Nguyễn Văn A",
  email: "nguyenvana@example.com",
  phoneNumber: "0981234567",
  method: "cod",
  amount: 500000,
  description: "Thanh toán đơn hàng giày",
  paymentDetails: {
    cardLastFour: "1234",
  },
};

async function runTests() {
  try {
    log("\n=== PAYMENT API TEST ===\n", "blue");

    // Test 1: Tạo thanh toán
    log("1. Testing CREATE PAYMENT...", "yellow");
    const createRes = await axios.post(BASE_URL, testPayment);
    const paymentId = createRes.data.payment._id;
    log(`✓ Thanh toán tạo thành công với ID: ${paymentId}`, "green");
    console.log("Response:", JSON.stringify(createRes.data, null, 2));

    // Test 2: Lấy danh sách thanh toán
    log("\n2. Testing GET ALL PAYMENTS...", "yellow");
    const listRes = await axios.get(BASE_URL);
    log(
      `✓ Lấy danh sách thành công (${listRes.data.total} thanh toán)`,
      "green"
    );
    console.log("Response:", JSON.stringify(listRes.data, null, 2));

    // Test 3: Lấy chi tiết thanh toán
    log("\n3. Testing GET PAYMENT BY ID...", "yellow");
    const getRes = await axios.get(`${BASE_URL}/${paymentId}`);
    log(`✓ Lấy chi tiết thanh toán thành công`, "green");
    console.log("Response:", JSON.stringify(getRes.data, null, 2));

    // Test 4: Cập nhật trạng thái thanh toán
    log("\n4. Testing UPDATE PAYMENT STATUS...", "yellow");
    const updateRes = await axios.put(`${BASE_URL}/${paymentId}`, {
      status: "completed",
    });
    log(`✓ Cập nhật trạng thái thành công`, "green");
    console.log("Response:", JSON.stringify(updateRes.data, null, 2));

    // Test 5: Lấy thống kê
    log("\n5. Testing GET PAYMENT STATS...", "yellow");
    const statsRes = await axios.get(`${BASE_URL}/stats`);
    log(`✓ Lấy thống kê thành công`, "green");
    console.log("Response:", JSON.stringify(statsRes.data, null, 2));

    // Test 6: Lấy danh sách với filter
    log("\n6. Testing GET PAYMENTS WITH FILTER...", "yellow");
    const filterRes = await axios.get(
      `${BASE_URL}?status=completed&method=cod&page=1&limit=5`
    );
    log(`✓ Lấy danh sách với filter thành công`, "green");
    console.log("Response:", JSON.stringify(filterRes.data, null, 2));

    // Test 7: Xóa thanh toán
    log("\n7. Testing DELETE PAYMENT...", "yellow");
    const deleteRes = await axios.delete(`${BASE_URL}/${paymentId}`);
    log(`✓ Xóa thanh toán thành công`, "green");
    console.log("Response:", JSON.stringify(deleteRes.data, null, 2));

    log("\n=== ALL TESTS PASSED ✓ ===\n", "green");
  } catch (error) {
    log(
      `\n✗ ERROR: ${error.response?.data?.message || error.message}`,
      "red"
    );
    console.error("Full error:", error.response?.data || error.message);
    process.exit(1);
  }
}

// Chạy tests
runTests();
