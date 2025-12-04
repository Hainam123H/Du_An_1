package com.poly.ban_giay_app.network.request;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderRequest {
    @SerializedName("items")
    private List<OrderItem> items;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("shipping_address")
    private String shippingAddress;

    @SerializedName("phone")
    private String phone;

    @SerializedName("note")
    private String note;

    public OrderRequest(List<OrderItem> items, String paymentMethod, String shippingAddress, String phone) {
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.phone = phone;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static class OrderItem {
        @SerializedName("product_id")
        private String productId;

        @SerializedName("quantity")
        private Integer quantity;

        @SerializedName("size")
        private String size;

        @SerializedName("price")
        private Integer price;

        public OrderItem(String productId, Integer quantity, String size, Integer price) {
            this.productId = productId;
            this.quantity = quantity;
            this.size = size;
            this.price = price;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }
    }
}







