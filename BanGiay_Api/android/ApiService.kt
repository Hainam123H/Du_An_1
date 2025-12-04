// ============================================
// FILE: ApiService.kt (Android Studio)
// Retrofit interface để gọi API Payment
// ============================================

package com.example.bangiay.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.example.bangiay.models.PaymentRequest
import com.example.bangiay.models.ApiResponse

interface ApiService {

    // ==================== PAYMENT ENDPOINTS ====================

    /**
     * Tạo đơn thanh toán
     * POST /api/payment/create-payment
     */
    @POST("payment/create-payment")
    suspend fun createPayment(@Body request: PaymentRequest): ApiResponse<PaymentResponse>

    /**
     * Thanh toán bằng thẻ tín dụng
     * POST /api/payment/process-credit-card
     */
    @POST("payment/process-credit-card")
    suspend fun processCreditCardPayment(@Body request: Map<String, Any>): ApiResponse<PaymentResponse>

    /**
     * Thanh toán bằng ATM
     * POST /api/payment/process-atm
     */
    @POST("payment/process-atm")
    suspend fun processATMPayment(@Body request: Map<String, Any>): ApiResponse<PaymentResponse>

    /**
     * Thanh toán khi nhận hàng (COD)
     * POST /api/payment/process-cod
     */
    @POST("payment/process-cod")
    suspend fun processCODPayment(@Body request: Map<String, Any>): ApiResponse<PaymentResponse>

    /**
     * Lấy chi tiết thanh toán
     * GET /api/payment/{paymentId}
     */
    @GET("payment/{paymentId}")
    suspend fun getPaymentDetails(@Path("paymentId") paymentId: String): ApiResponse<PaymentResponse>

    /**
     * Xác nhận thanh toán
     * PUT /api/payment/{paymentId}/confirm
     */
    @PUT("payment/{paymentId}/confirm")
    suspend fun confirmPayment(@Path("paymentId") paymentId: String): ApiResponse<PaymentResponse>

    /**
     * Hủy thanh toán
     * PUT /api/payment/{paymentId}/cancel
     */
    @PUT("payment/{paymentId}/cancel")
    suspend fun cancelPayment(@Path("paymentId") paymentId: String, @Body request: Map<String, String>): ApiResponse<PaymentResponse>

    /**
     * Lấy danh sách thanh toán
     * GET /api/payment?userId=...&status=...&method=...&page=1&limit=10
     */
    @GET("payment")
    suspend fun listPayments(): ApiResponse<PaymentListResponse>

    /**
     * Lấy thống kê thanh toán
     * GET /api/payment/stats/overview
     */
    @GET("payment/stats/overview")
    suspend fun getPaymentStats(): ApiResponse<PaymentStatsResponse>
}

// ==================== DATA MODELS ====================

/**
 * Request model để tạo payment
 */
data class PaymentRequest(
    val orderId: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val method: String, // "credit_card", "atm", "cod"
    val amount: Long,
    val description: String? = null,
    val paymentDetails: Map<String, String>? = null
)

/**
 * Response model cho payment
 */
data class PaymentResponse(
    val _id: String,
    val orderId: String,
    val userId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val method: String,
    val amount: Long,
    val status: String,
    val transactionId: String,
    val createdAt: String,
    val updatedAt: String
)

/**
 * Response model cho danh sách payments
 */
data class PaymentListResponse(
    val total: Int,
    val page: Int,
    val pages: Int,
    val payments: List<PaymentResponse>
)

/**
 * Response model cho thống kê
 */
data class PaymentStatsResponse(
    val byStatus: List<StatusStat>,
    val byMethod: List<MethodStat>,
    val totalRevenue: List<RevenueStat>
)

data class StatusStat(
    val _id: String,
    val count: Int,
    val totalAmount: Long
)

data class MethodStat(
    val _id: String,
    val count: Int,
    val totalAmount: Long
)

data class RevenueStat(
    val _id: String?,
    val total: Long
)

/**
 * Generic API Response wrapper
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
