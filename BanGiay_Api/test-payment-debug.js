const http = require("http");

const testData = {
  fullName: "Nguyễn Văn A",
  email: "nguyenvana@example.com",
  phoneNumber: "0981234567",
  method: "cod",
  amount: 500000,
};

function makeRequest(method, path, data = null) {
  return new Promise((resolve, reject) => {
    const options = {
      hostname: "localhost",
      port: 3000,
      path: path,
      method: method,
      headers: {
        "Content-Type": "application/json",
      },
    };

    const req = http.request(options, (res) => {
      let responseData = "";

      res.on("data", (chunk) => {
        responseData += chunk;
      });

      res.on("end", () => {
        resolve({
          status: res.statusCode,
          data: responseData,
        });
      });
    });

    req.on("error", (error) => {
      reject(error);
    });

    if (data) {
      req.write(JSON.stringify(data));
    }

    req.end();
  });
}

async function runTests() {
  console.log("\n=== PAYMENT API TEST ===\n");

  try {
    // Test 1: Tạo thanh toán
    console.log("1. Testing CREATE PAYMENT...");
    const createRes = await makeRequest("POST", "/api/payment", testData);
    console.log(`Status: ${createRes.status}`);
    console.log("Raw response:", createRes.data);

    let paymentData;
    try {
      paymentData = JSON.parse(createRes.data);
    } catch (e) {
      console.error("Parse error:", e.message);
      console.error("Raw data was:", createRes.data);
      throw e;
    }

    console.log("Parsed Response:", JSON.stringify(paymentData, null, 2));

    if (!paymentData.payment || !paymentData.payment._id) {
      throw new Error("Không tạo được payment");
    }

    const paymentId = paymentData.payment._id;
    console.log(`✓ Payment ID: ${paymentId}\n`);

    // Test 2: Lấy danh sách
    console.log("2. Testing GET ALL PAYMENTS...");
    const listRes = await makeRequest("GET", "/api/payment");
    console.log(`Status: ${listRes.status}`);
    const listData = JSON.parse(listRes.data);
    console.log(`✓ Total payments: ${listData.total}\n`);

    // Test 3: Lấy chi tiết
    console.log("3. Testing GET PAYMENT BY ID...");
    const getRes = await makeRequest("GET", `/api/payment/${paymentId}`);
    console.log(`Status: ${getRes.status}`);
    const getData = JSON.parse(getRes.data);
    console.log(`✓ Payment details retrieved\n`);

    // Test 4: Cập nhật status
    console.log("4. Testing UPDATE PAYMENT STATUS...");
    const updateRes = await makeRequest("PUT", `/api/payment/${paymentId}`, {
      status: "completed",
    });
    console.log(`Status: ${updateRes.status}`);
    const updateData = JSON.parse(updateRes.data);
    console.log(`✓ Payment status updated\n`);

    // Test 5: Lấy stats
    console.log("5. Testing GET PAYMENT STATS...");
    const statsRes = await makeRequest("GET", "/api/payment/stats");
    console.log(`Status: ${statsRes.status}`);
    const statsData = JSON.parse(statsRes.data);
    console.log(`✓ Stats retrieved\n`);

    console.log("=== ALL TESTS PASSED ✓ ===\n");
    process.exit(0);
  } catch (error) {
    console.error("✗ ERROR:", error.message);
    console.error("Stack:", error.stack);
    process.exit(1);
  }
}

runTests();
