const router = require("express").Router();
const paymentController = require("../controllers/payment.controller");

// ==================== PAYMENT ENDPOINTS ====================

// POST /api/payment/create-payment - Tạo đơn thanh toán
router.post("/create-payment", paymentController.createPayment);

// POST /api/payment/process-credit-card - Thanh toán bằng thẻ tín dụng
router.post("/process-credit-card", paymentController.processCreditCardPayment);

// POST /api/payment/process-atm - Thanh toán bằng thẻ ATM
router.post("/process-atm", paymentController.processATMPayment);

// POST /api/payment/process-cod - Thanh toán khi nhận hàng (COD)
router.post("/process-cod", paymentController.processCODPayment);

// GET /api/payment/:paymentId - Lấy chi tiết thanh toán
router.get("/:paymentId", paymentController.getPaymentDetails);

// PUT /api/payment/:paymentId/confirm - Xác nhận thanh toán
router.put("/:paymentId/confirm", paymentController.confirmPayment);

// PUT /api/payment/:paymentId/cancel - Hủy thanh toán
router.put("/:paymentId/cancel", paymentController.cancelPayment);

// GET /api/payment - Lấy danh sách thanh toán
router.get("/", paymentController.listPayments);

// GET /api/payment/stats/overview - Lấy thống kê thanh toán
router.get("/stats/overview", paymentController.getPaymentStats);

module.exports = router;

