package com.psycology.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "razorpay_order_mappings", indexes = @Index(columnList = "razorpayOrderId", unique = true))
public class RazorpayOrderMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(nullable = false)
    private String orderType; // course | shop

    @Column(nullable = false)
    private Long entityId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
}
