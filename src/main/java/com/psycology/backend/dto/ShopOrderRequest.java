package com.psycology.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class ShopOrderRequest {

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<ShopOrderItemRequest> items;

    @NotNull(message = "Total amount is required")
    @Min(0)
    private Double totalAmount;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @NotNull(message = "Shipping address is required")
    @Valid
    private ShippingAddressRequest shippingAddress;

    private String paymentStatus = "pending";
    private String paymentId;

    // Getters and Setters
    public List<ShopOrderItemRequest> getItems() { return items; }
    public void setItems(List<ShopOrderItemRequest> items) { this.items = items; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public ShippingAddressRequest getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(ShippingAddressRequest shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public static class ShopOrderItemRequest {
        @NotNull(message = "Shop item ID is required")
        private Long shopItemId;
        private String title;
        private String type;

        @NotNull(message = "Price is required")
        @Min(0)
        private Double price;

        @Min(1)
        private Integer quantity = 1;

        public Long getShopItemId() { return shopItemId; }
        public void setShopItemId(Long shopItemId) { this.shopItemId = shopItemId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class ShippingAddressRequest {
        @NotBlank(message = "Address line 1 is required")
        private String line1;
        private String line2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Pincode is required")
        private String pincode;

        public String getLine1() { return line1; }
        public void setLine1(String line1) { this.line1 = line1; }
        public String getLine2() { return line2; }
        public void setLine2(String line2) { this.line2 = line2; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPincode() { return pincode; }
        public void setPincode(String pincode) { this.pincode = pincode; }
    }
}
