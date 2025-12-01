const mongoose = require("mongoose");
const Payment = require("./models/Payment");
const Order = require("./models/Order");
const User = require("./models/User");
require("dotenv").config();

// Mock dữ liệu thanh toán
const seedPayments = [
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860ea"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439011"),
    fullName: "Nguyễn Văn A",
    email: "nguyenvana@example.com",
    phoneNumber: "0981234567",
    method: "cod",
    amount: 500000,
    status: "completed",
    transactionId: `PAY_${Date.now()}_cod1`,
    paymentDetails: {
      deliveryAddress: "123 Nguyễn Huệ, Q.1, TP.HCM",
    },
    description: "Thanh toán đơn hàng giày Nike",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860eb"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439012"),
    fullName: "Trần Thị B",
    email: "tranthib@example.com",
    phoneNumber: "0912345678",
    method: "credit_card",
    amount: 750000,
    status: "completed",
    transactionId: `PAY_${Date.now()}_cc1`,
    paymentDetails: {
      cardLastFour: "1234",
      cardHolder: "TRAN THI B",
      cardExpiry: "12/25",
    },
    description: "Thanh toán đơn hàng giày Adidas",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860ec"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439013"),
    fullName: "Lê Văn C",
    email: "levanc@example.com",
    phoneNumber: "0933445566",
    method: "atm",
    amount: 1200000,
    status: "completed",
    transactionId: `PAY_${Date.now()}_atm1`,
    paymentDetails: {
      atmBankCode: "VIETCOMBANK",
      atmBankName: "Vietcombank",
      atmAccountNumber: "12345678901234",
    },
    description: "Thanh toán đơn hàng giày Converse",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860ed"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439014"),
    fullName: "Phạm Văn D",
    email: "phamvand@example.com",
    phoneNumber: "0944556677",
    method: "cod",
    amount: 350000,
    status: "pending",
    transactionId: `PAY_${Date.now()}_cod2`,
    paymentDetails: {
      deliveryAddress: "456 Lê Lợi, Q.3, TP.HCM",
    },
    description: "Thanh toán đơn hàng giày Puma",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860ee"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439015"),
    fullName: "Hoàng Thị E",
    email: "hoangthie@example.com",
    phoneNumber: "0955667788",
    method: "credit_card",
    amount: 650000,
    status: "completed",
    transactionId: `PAY_${Date.now()}_cc2`,
    paymentDetails: {
      cardLastFour: "5678",
      cardHolder: "HOANG THI E",
      cardExpiry: "08/26",
    },
    description: "Thanh toán đơn hàng giày New Balance",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860ef"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439016"),
    fullName: "Võ Văn F",
    email: "vovanf@example.com",
    phoneNumber: "0966778899",
    method: "atm",
    amount: 900000,
    status: "completed",
    transactionId: `PAY_${Date.now()}_atm2`,
    paymentDetails: {
      atmBankCode: "TECHCOMBANK",
      atmBankName: "Techcombank",
      atmAccountNumber: "98765432109876",
    },
    description: "Thanh toán đơn hàng giày Vans",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860f0"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439017"),
    fullName: "Dương Thị G",
    email: "duongthig@example.com",
    phoneNumber: "0977889900",
    method: "cod",
    amount: 420000,
    status: "failed",
    transactionId: `PAY_${Date.now()}_cod3`,
    paymentDetails: {
      deliveryAddress: "789 Đinh Tiên Hoàng, Q.5, TP.HCM",
    },
    description: "Thanh toán đơn hàng giày Skechers - Thất bại",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860f1"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439018"),
    fullName: "Bùi Văn H",
    email: "buivanh@example.com",
    phoneNumber: "0988990011",
    method: "credit_card",
    amount: 1100000,
    status: "cancelled",
    transactionId: `PAY_${Date.now()}_cc3`,
    paymentDetails: {
      cardLastFour: "9012",
      cardHolder: "BUI VAN H",
      cardExpiry: "05/27",
    },
    description: "Thanh toán đơn hàng giày Timberland - Bị hủy",
  },
  {
    orderId: new mongoose.Types.ObjectId("507f191e810c19729de860f2"),
    userId: new mongoose.Types.ObjectId("507f1f77bcf86cd799439019"),
    fullName: "Cao Thị I",
    email: "caothii@example.com",
    phoneNumber: "0999001122",
    method: "atm",
    amount: 580000,
    status: "processing",
    transactionId: `PAY_${Date.now()}_atm3`,
    paymentDetails: {
      atmBankCode: "BIDV",
      atmBankName: "BIDV",
      atmAccountNumber: "45678901234567",
    },
    description: "Thanh toán đơn hàng giày Dr. Martens - Đang xử lý",
  },
];

async function seedDatabase() {
  try {
    const mongoUri =
      process.env.MONGODB_URI || "mongodb://localhost:27017/BanGiay_App";

    await mongoose.connect(mongoUri);
    console.log("✅ Kết nối MongoDB thành công!");

    // Xóa dữ liệu cũ
    await Payment.deleteMany({});
    console.log("🗑️  Xóa dữ liệu cũ thành công!");

    // Thêm dữ liệu mới
    const result = await Payment.insertMany(seedPayments);
    console.log(`✅ Thêm ${result.length} thanh toán thành công!`);

    // Hiển thị dữ liệu
    const allPayments = await Payment.find()
      .sort({ createdAt: -1 });

    console.log("\n📊 Danh sách thanh toán:");
    console.log("═".repeat(80));
    allPayments.forEach((payment, index) => {
      console.log(`\n${index + 1}. ${payment.fullName}`);
      console.log(`   Email: ${payment.email}`);
      console.log(`   Phương thức: ${payment.method}`);
      console.log(`   Số tiền: ${payment.amount.toLocaleString("vi-VN")} VNĐ`);
      console.log(`   Trạng thái: ${payment.status}`);
      console.log(`   Transaction ID: ${payment.transactionId}`);
    });

    console.log("\n═".repeat(80));
    console.log("\n✨ Seed data hoàn thành!");
    console.log(`📌 Tổng cộng: ${allPayments.length} thanh toán`);

    // Thống kê
    const stats = await Payment.aggregate([
      {
        $group: {
          _id: "$status",
          count: { $sum: 1 },
          totalAmount: { $sum: "$amount" },
        },
      },
    ]);

    console.log("\n📈 Thống kê theo trạng thái:");
    stats.forEach((stat) => {
      console.log(
        `   ${stat._id}: ${stat.count} đơn (${stat.totalAmount.toLocaleString("vi-VN")} VNĐ)`
      );
    });

    const methodStats = await Payment.aggregate([
      {
        $group: {
          _id: "$method",
          count: { $sum: 1 },
          totalAmount: { $sum: "$amount" },
        },
      },
    ]);

    console.log("\n📈 Thống kê theo phương thức:");
    methodStats.forEach((method) => {
      console.log(
        `   ${method._id}: ${method.count} đơn (${method.totalAmount.toLocaleString("vi-VN")} VNĐ)`
      );
    });

    process.exit(0);
  } catch (error) {
    console.error("❌ Lỗi:", error.message);
    process.exit(1);
  }
}

seedDatabase();
