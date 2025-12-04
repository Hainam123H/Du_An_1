const Order = require("../models/Order");
const Product = require("../models/Product");
const User = require("../models/User");

// ======================== CREATE ORDER ========================
// POST /api/order
// Tạo đơn hàng mới
exports.createOrder = async (req, res) => {
  try {
    const { items, payment_method, shipping_address, phone, note } = req.body;
    const userId = req.userId; // Lấy từ middleware verifyToken

    // Validate input
    if (!items || !Array.isArray(items) || items.length === 0) {
      return res.status(400).json({
        success: false,
        message: "Danh sách sản phẩm không hợp lệ",
      });
    }

    // Validate và tính tổng tiền
    let tongTien = 0;
    const orderItems = [];

    for (const item of items) {
      const { product_id, quantity, size, price } = item;

      if (!product_id || !quantity || !price) {
        return res.status(400).json({
          success: false,
          message: "Thông tin sản phẩm không đủ",
        });
      }

      // Kiểm tra sản phẩm có tồn tại không
      const product = await Product.findById(product_id);
      if (!product) {
        return res.status(404).json({
          success: false,
          message: `Không tìm thấy sản phẩm với ID: ${product_id}`,
        });
      }

      // Tính tổng tiền
      const itemTotal = quantity * price;
      tongTien += itemTotal;

      // Tạo order item theo schema của Order model
      orderItems.push({
        san_pham_id: product_id,
        ten_san_pham: product.ten_san_pham || "Sản phẩm không tên",
        so_luong: quantity,
        kich_thuoc: size || "Mặc định",
        gia: price,
      });
    }

    // Tạo đơn hàng
    const order = await Order.create({
      user_id: userId,
      items: orderItems,
      tong_tien: tongTien,
      dia_chi_giao_hang: shipping_address || "Chưa cập nhật",
      so_dien_thoai: phone || "Chưa cập nhật",
      ghi_chu: note || "",
      trang_thai: "pending",
    });

    // Populate để lấy thông tin chi tiết
    await order.populate("user_id items.san_pham_id");

    // Map sang format response mà Android app mong đợi
    const responseData = mapOrderToResponse(order, payment_method);

    return res.status(201).json({
      success: true,
      message: "Tạo đơn hàng thành công",
      data: responseData,
    });
  } catch (error) {
    console.error("createOrder error:", error);
    return res.status(500).json({
      success: false,
      message: "Không thể tạo đơn hàng",
      error: error.message,
    });
  }
};

// ======================== GET MY ORDERS ========================
// GET /api/order
// Lấy danh sách đơn hàng của user hiện tại
exports.getMyOrders = async (req, res) => {
  try {
    const userId = req.userId;

    const orders = await Order.find({ user_id: userId })
      .populate("items.san_pham_id")
      .sort({ createdAt: -1 });

    const responseData = orders.map((order) => mapOrderToResponse(order));

    return res.json({
      success: true,
      data: responseData,
    });
  } catch (error) {
    console.error("getMyOrders error:", error);
    return res.status(500).json({
      success: false,
      message: "Không thể lấy danh sách đơn hàng",
      error: error.message,
    });
  }
};

// ======================== GET ORDER BY ID ========================
// GET /api/order/:id
// Lấy chi tiết đơn hàng theo ID
exports.getOrderById = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const order = await Order.findById(id).populate("items.san_pham_id");

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    // Kiểm tra đơn hàng thuộc về user hiện tại
    if (order.user_id.toString() !== userId.toString()) {
      return res.status(403).json({
        success: false,
        message: "Bạn không có quyền xem đơn hàng này",
      });
    }

    const responseData = mapOrderToResponse(order);

    return res.json({
      success: true,
      data: responseData,
    });
  } catch (error) {
    console.error("getOrderById error:", error);
    return res.status(500).json({
      success: false,
      message: "Không thể lấy thông tin đơn hàng",
      error: error.message,
    });
  }
};

// ======================== CANCEL ORDER ========================
// PUT /api/order/:id/cancel
// Hủy đơn hàng
exports.cancelOrder = async (req, res) => {
  try {
    const { id } = req.params;
    const userId = req.userId;

    const order = await Order.findById(id);

    if (!order) {
      return res.status(404).json({
        success: false,
        message: "Không tìm thấy đơn hàng",
      });
    }

    // Kiểm tra đơn hàng thuộc về user hiện tại
    if (order.user_id.toString() !== userId.toString()) {
      return res.status(403).json({
        success: false,
        message: "Bạn không có quyền hủy đơn hàng này",
      });
    }

    // Chỉ cho phép hủy đơn hàng ở trạng thái pending hoặc confirmed
    if (order.trang_thai === "cancelled") {
      return res.status(400).json({
        success: false,
        message: "Đơn hàng đã được hủy trước đó",
      });
    }

    if (order.trang_thai === "delivered") {
      return res.status(400).json({
        success: false,
        message: "Không thể hủy đơn hàng đã được giao",
      });
    }

    // Cập nhật trạng thái
    order.trang_thai = "cancelled";
    await order.save();

    await order.populate("items.san_pham_id");
    const responseData = mapOrderToResponse(order);

    return res.json({
      success: true,
      message: "Hủy đơn hàng thành công",
      data: responseData,
    });
  } catch (error) {
    console.error("cancelOrder error:", error);
    return res.status(500).json({
      success: false,
      message: "Không thể hủy đơn hàng",
      error: error.message,
    });
  }
};

// ======================== HELPER FUNCTIONS ========================
// Map Order từ database format sang response format mà Android app mong đợi
function mapOrderToResponse(order, paymentMethod = null) {
  // Map items
  const mappedItems = order.items.map((item) => {
    const product = item.san_pham_id;
    return {
      product_id: item.san_pham_id._id?.toString() || item.san_pham_id?.toString(),
      product_name: item.ten_san_pham || (product?.ten_san_pham || "Sản phẩm không tên"),
      quantity: item.so_luong,
      price: item.gia,
      size: item.kich_thuoc,
    };
  });

  // Map order status
  const statusMap = {
    pending: "pending",
    confirmed: "confirmed",
    shipping: "shipping",
    delivered: "delivered",
    cancelled: "cancelled",
  };

  return {
    _id: order._id.toString(),
    order_id: order._id.toString(), // Dùng _id làm order_id
    user_id: order.user_id._id?.toString() || order.user_id?.toString(),
    items: mappedItems,
    total_amount: order.tong_tien,
    payment_method: paymentMethod || "unknown",
    payment_status: "pending", // Mặc định, có thể cập nhật từ Payment model sau
    order_status: statusMap[order.trang_thai] || order.trang_thai,
    shipping_address: order.dia_chi_giao_hang || "Chưa cập nhật",
    phone: order.so_dien_thoai || "Chưa cập nhật",
    created_at: order.createdAt?.toISOString() || new Date().toISOString(),
    updated_at: order.updatedAt?.toISOString() || new Date().toISOString(),
  };
}

