package com.poly.ban_giay_app.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {
    @SerializedName("_id")
    private String id;

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("total_amount")
    private Integer totalAmount;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("payment_status")
    private String paymentStatus;

    @SerializedName("order_status")
    private String orderStatus;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("phone")
    private String phone;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Getters
    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    // Inner class for order items
    public static class OrderItem {
        @SerializedName("product_id")
        private String productId;

        @SerializedName("product_name")
        private String productName;

        @SerializedName("quantity")
        private Integer quantity;

        @SerializedName("price")
        private Integer price;

        @SerializedName("size")
        private String size;

        public String getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Integer getPrice() {
            return price;
        }

        public String getSize() {
            return size;
        }
    }
}







