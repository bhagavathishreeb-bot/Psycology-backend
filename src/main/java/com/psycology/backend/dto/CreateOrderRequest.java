package com.psycology.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateOrderRequest {

    /**
     * Amount in rupees (will be converted to paise for Razorpay).
     * E.g. 599 for ₹599
     */
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least ₹1")
    private Double amount;

    @NotBlank(message = "Receipt is required")
    private String receipt;

    /**
     * Type of order: "course" or "shop"
     */
    @NotBlank(message = "Order type is required")
    private String orderType;

    /**
     * Your internal entity ID (course_purchase_id or shop_order_id)
     */
    @NotNull(message = "Entity ID is required")
    private Long entityId;

    private String customerName;
    private String customerEmail;

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getReceipt() { return receipt; }
    public void setReceipt(String receipt) { this.receipt = receipt; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}
