const Payment = require("../models/Payment");
const Order = require("../models/Order");

// ======================== CREATE PAYMENT ========================
// POST /api/payment/create-payment
// Tạo thanh toán khi người dùng bấm nút mua
exports.createPayment = async (req, res) => {
  try {
    const {
      orderId,
      userId,
      fullName,
      email,
      phoneNumber,
      method, // "credit_card", "atm", "cod"
      amount,
      paymentDetails,
    } = req.body;

    // Validate input
    if (
      !orderId ||
      !userId ||
      !fullName ||
      !email ||
      !phoneNumber ||
      !method ||
      amount === undefined
    ) {
      return res.status(400).json({
        success: false,
        message: "Thông tin thanh toán không đủ",
      });
    }

    // Kiểm tra phương thức thanh toán hợp lệ
    const validMethods = ["credit_card", "atm", "cod"];
    if (!validMethods.includes(method)) {
      return res.status(400).json({
        success: false,
        message: "Phương thức thanh toán không hợp lệ",
      });
    }

    // Kiểm tra Order có tồn tại
    const order = await Order.findById(orderId);
    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    // Tạo transactionId duy nhất
    const transactionId = `PAY_${Date.now()}_${Math.random()
      .toString(36)
      .substr(2, 9)}`;

    // Tạo payment
    const payment = await Payment.create({
      orderId,
      userId,
      fullName,
      email,
      phoneNumber,
      method,
      amount,
      transactionId,
      status: "pending",
      paymentDetails,
    });

    // Populate để trả về thông tin chi tiết
    await payment.populate("orderId userId");

    return res.status(201).json({
      success: true,
      message: "Tạo đơn thanh toán thành công",
      data: {
        paymentId: payment._id,
        transactionId: payment.transactionId,
        method: payment.method,
        amount: payment.amount,
        status: payment.status,
        payment,
      },
    });
  } catch (error) {
    console.error("createPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Không thể tạo đơn thanh toán",
      error: error.message,
    });
  }
};

// ======================== PROCESS PAYMENT BY METHOD ========================
// POST /api/payment/process-credit-card
// Xử lý thanh toán bằng thẻ tín dụng
exports.processCreditCardPayment = async (req, res) => {
  try {
    const { paymentId, cardNumber, cardExpiry, cvv, cardHolder } = req.body;

    if (!paymentId || !cardNumber || !cardExpiry || !cvv || !cardHolder) {
      return res.status(400).json({
        success: false,
        message: "Thông tin thẻ không đầy đủ",
      });
    }

    // Tìm payment
    const payment = await Payment.findById(paymentId);
    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    // Giả lập xác minh thẻ (trong thực tế gọi gateway như Stripe, Square, etc.)
    // Validate card format
    if (!/^\d{16}$/.test(cardNumber.replace(/\s/g, ""))) {
      return res.status(400).json({
        success: false,
        message: "Số thẻ không hợp lệ",
      });
    }

    if (!/^\d{3,4}$/.test(cvv)) {
      return res.status(400).json({
        success: false,
        message: "CVV không hợp lệ",
      });
    }

    // Cập nhật payment
    const cardLastFour = cardNumber.slice(-4);
    payment.method = "credit_card";
    payment.status = "processing";
    payment.paymentDetails = {
      cardLastFour,
      cardHolder,
      cardExpiry,
    };
    await payment.save();

    // Giả lập xử lý (thực tế: gọi payment gateway)
    // Sau 2 giây, update trạng thái thành completed
    setTimeout(async () => {
      payment.status = "completed";
      await payment.save();
      console.log(`Payment ${paymentId} completed via credit card`);
    }, 2000);

    return res.status(200).json({
      success: true,
      message: "Đang xử lý thanh toán bằng thẻ tín dụng",
      data: {
        paymentId: payment._id,
        status: payment.status,
        method: "credit_card",
        amount: payment.amount,
      },
    });
  } catch (error) {
    console.error("processCreditCardPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi xử lý thanh toán thẻ tín dụng",
      error: error.message,
    });
  }
};

// POST /api/payment/process-atm
// Xử lý thanh toán bằng thẻ ATM
exports.processATMPayment = async (req, res) => {
  try {
    const { paymentId, atmBankCode, atmAccountNumber, atmBankName } = req.body;

    if (!paymentId || !atmBankCode || !atmAccountNumber) {
      return res.status(400).json({
        success: false,
        message: "Thông tin ATM không đầy đủ",
      });
    }

    // Tìm payment
    const payment = await Payment.findById(paymentId);
    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    // Validate account number
    if (!/^\d{8,20}$/.test(atmAccountNumber)) {
      return res.status(400).json({
        success: false,
        message: "Số tài khoản ATM không hợp lệ",
      });
    }

    // Cập nhật payment
    payment.method = "atm";
    payment.status = "processing";
    payment.paymentDetails = {
      atmBankCode,
      atmBankName,
      atmAccountNumber,
    };
    await payment.save();

    // Giả lập xử lý qua ngân hàng
    setTimeout(async () => {
      payment.status = "completed";
      await payment.save();
      console.log(`Payment ${paymentId} completed via ATM`);
    }, 3000);

    return res.status(200).json({
      success: true,
      message: "Đang xử lý thanh toán bằng thẻ ATM",
      data: {
        paymentId: payment._id,
        status: payment.status,
        method: "atm",
        amount: payment.amount,
        bankCode: atmBankCode,
      },
    });
  } catch (error) {
    console.error("processATMPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi xử lý thanh toán ATM",
      error: error.message,
    });
  }
};

// POST /api/payment/process-cod
// Xử lý thanh toán khi nhận hàng (COD - Cash On Delivery)
exports.processCODPayment = async (req, res) => {
  try {
    const { paymentId, deliveryAddress, notes } = req.body;

    if (!paymentId) {
      return res.status(400).json({
        success: false,
        message: "PaymentId không hợp lệ",
      });
    }

    // Tìm payment
    const payment = await Payment.findById(paymentId);
    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    // Cập nhật payment
    payment.method = "cod";
    payment.status = "pending"; // COD sẽ hoàn thành sau khi giao hàng
    payment.paymentDetails = {
      deliveryAddress: deliveryAddress || payment.paymentDetails?.deliveryAddress,
    };
    payment.notes = notes || "";
    await payment.save();

    return res.status(200).json({
      success: true,
      message: "Đơn hàng sẽ được thanh toán khi nhận hàng",
      data: {
        paymentId: payment._id,
        status: payment.status,
        method: "cod",
        amount: payment.amount,
        deliveryAddress: payment.paymentDetails.deliveryAddress,
      },
    });
  } catch (error) {
    console.error("processCODPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi xử lý thanh toán COD",
      error: error.message,
    });
  }
};

// ======================== GET PAYMENT ========================
// GET /api/payment/:paymentId
// Lấy chi tiết thanh toán
exports.getPaymentDetails = async (req, res) => {
  try {
    const { paymentId } = req.params;

    const payment = await Payment.findById(paymentId)
      .populate("orderId", "_id tong_tien trang_thai items")
      .populate("userId", "fullName email phoneNumber");

    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    return res.status(200).json({
      success: true,
      data: payment,
    });
  } catch (error) {
    console.error("getPaymentDetails error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi lấy chi tiết thanh toán",
      error: error.message,
    });
  }
};

// ======================== CONFIRM PAYMENT ========================
// PUT /api/payment/:paymentId/confirm
// Xác nhận thanh toán thành công
exports.confirmPayment = async (req, res) => {
  try {
    const { paymentId } = req.params;

    const payment = await Payment.findByIdAndUpdate(
      paymentId,
      { status: "completed" },
      { new: true }
    );

    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    // Cập nhật trạng thái Order
    await Order.findByIdAndUpdate(payment.orderId, {
      trang_thai: "confirmed",
    });

    return res.status(200).json({
      success: true,
      message: "Thanh toán đã được xác nhận",
      data: payment,
    });
  } catch (error) {
    console.error("confirmPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi xác nhận thanh toán",
      error: error.message,
    });
  }
};

// ======================== CANCEL PAYMENT ========================
// PUT /api/payment/:paymentId/cancel
// Hủy thanh toán
exports.cancelPayment = async (req, res) => {
  try {
    const { paymentId } = req.params;
    const { reason } = req.body;

    const payment = await Payment.findByIdAndUpdate(
      paymentId,
      {
        status: "cancelled",
        notes: reason || "Thanh toán bị hủy",
      },
      { new: true }
    );

    if (!payment) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn thanh toán",
      });
    }

    // Cập nhật trạng thái Order thành cancelled
    await Order.findByIdAndUpdate(payment.orderId, {
      trang_thai: "cancelled",
    });

    return res.status(200).json({
      success: true,
      message: "Thanh toán đã bị hủy",
      data: payment,
    });
  } catch (error) {
    console.error("cancelPayment error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi hủy thanh toán",
      error: error.message,
    });
  }
};

// ======================== LIST PAYMENTS ========================
// GET /api/payment
// Lấy danh sách thanh toán
exports.listPayments = async (req, res) => {
  try {
    const { userId, status, method, page = 1, limit = 10 } = req.query;

    let filter = {};
    if (userId) filter.userId = userId;
    if (status) filter.status = status;
    if (method) filter.method = method;

    const skip = (page - 1) * limit;

    const payments = await Payment.find(filter)
      .populate("orderId", "_id tong_tien")
      .populate("userId", "fullName")
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Payment.countDocuments(filter);

    return res.status(200).json({
      success: true,
      data: {
        total,
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        payments,
      },
    });
  } catch (error) {
    console.error("listPayments error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi lấy danh sách thanh toán",
      error: error.message,
    });
  }
};

// ======================== PAYMENT STATS ========================
// GET /api/payment/stats/overview
// Thống kê thanh toán
exports.getPaymentStats = async (req, res) => {
  try {
    const stats = {
      byStatus: await Payment.aggregate([
        {
          $group: {
            _id: "$status",
            count: { $sum: 1 },
            totalAmount: { $sum: "$amount" },
          },
        },
      ]),
      byMethod: await Payment.aggregate([
        {
          $group: {
            _id: "$method",
            count: { $sum: 1 },
            totalAmount: { $sum: "$amount" },
          },
        },
      ]),
      totalRevenue: await Payment.aggregate([
        {
          $match: { status: "completed" },
        },
        {
          $group: {
            _id: null,
            total: { $sum: "$amount" },
          },
        },
      ]),
    };

    return res.status(200).json({
      success: true,
      data: stats,
    });
  } catch (error) {
    console.error("getPaymentStats error:", error);
    return res.status(500).json({
      success: false,
      message: "Lỗi lấy thống kê thanh toán",
      error: error.message,
    });
  }
};

