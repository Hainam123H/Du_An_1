const router = require("express").Router();
const orderController = require("../controllers/order.controller");
const { verifyToken } = require("../middleware/auth.middleware");

// ==================== ORDER ENDPOINTS ====================

// POST /api/order - Tạo đơn hàng mới (yêu cầu đăng nhập)
router.post("/", verifyToken, orderController.createOrder);

// GET /api/order - Lấy danh sách đơn hàng của user hiện tại (yêu cầu đăng nhập)
router.get("/", verifyToken, orderController.getMyOrders);

// GET /api/order/:id - Lấy chi tiết đơn hàng theo ID (yêu cầu đăng nhập)
router.get("/:id", verifyToken, orderController.getOrderById);

// PUT /api/order/:id/cancel - Hủy đơn hàng (yêu cầu đăng nhập)
router.put("/:id/cancel", verifyToken, orderController.cancelOrder);

module.exports = router;

