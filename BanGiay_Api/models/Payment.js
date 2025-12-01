const mongoose = require("mongoose");

const paymentSchema = new mongoose.Schema(
  {
    // Liên kết Order
    orderId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Order",
      required: true,
    },
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    
    // Thông tin khách hàng
    fullName: {
      type: String,
      required: true,
      trim: true,
    },
    email: {
      type: String,
      required: true,
      trim: true,
      lowercase: true,
    },
    phoneNumber: {
      type: String,
      required: true,
      trim: true,
    },
    
    // Thông tin thanh toán - 3 phương thức
    method: {
      type: String,
      required: true,
      enum: ["credit_card", "atm", "cod"], // Theo UI: Thẻ tín dụng, Thẻ ATM, Thanh toán khi nhận hàng
      default: "cod",
    },
    
    // Số tiền
    amount: {
      type: Number,
      required: true,
      min: 0,
    },
    
    // Trạng thái
    status: {
      type: String,
      enum: ["pending", "processing", "completed", "failed", "cancelled"],
      default: "pending",
    },
    
    // ID giao dịch tự động
    transactionId: {
      type: String,
      unique: true,
      sparse: true,
      index: true,
    },
    
    // Chi tiết phương thức thanh toán
    paymentDetails: {
      // Cho credit_card
      cardNumber: String,
      cardLastFour: String,
      cardExpiry: String,
      cardHolder: String,
      
      // Cho ATM
      atmBankCode: String,
      atmBankName: String,
      atmAccountNumber: String,
      
      // Cho COD
      deliveryAddress: String,
    },
    
    // Thông tin thêm
    description: String,
    notes: String,
  },
  {
    timestamps: true,
  }
);

// Index để tìm kiếm nhanh (xóa transactionId vì đã có index: true trong schema)
paymentSchema.index({ orderId: 1 });
paymentSchema.index({ userId: 1 });
paymentSchema.index({ status: 1 });
paymentSchema.index({ method: 1 });
paymentSchema.index({ createdAt: -1 });

module.exports = mongoose.model("Payment", paymentSchema);

