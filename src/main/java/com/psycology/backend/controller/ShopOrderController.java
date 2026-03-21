package com.psycology.backend.controller;

import com.psycology.backend.dto.ShopOrderRequest;
import com.psycology.backend.entity.ShopOrder;
import com.psycology.backend.entity.ShopOrderItem;
import com.psycology.backend.repository.ShopOrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop-orders")
public class ShopOrderController {

    private final ShopOrderRepository shopOrderRepository;

    public ShopOrderController(ShopOrderRepository shopOrderRepository) {
        this.shopOrderRepository = shopOrderRepository;
    }

    @PostMapping
    public ResponseEntity<?> createShopOrder(@Valid @RequestBody ShopOrderRequest request) {
        ShopOrder order = new ShopOrder();
        order.setTotalAmount(request.getTotalAmount());
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingLine1(request.getShippingAddress().getLine1());
        order.setShippingLine2(request.getShippingAddress().getLine2());
        order.setShippingCity(request.getShippingAddress().getCity());
        order.setShippingState(request.getShippingAddress().getState());
        order.setShippingPincode(request.getShippingAddress().getPincode());
        order.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "pending");
        order.setPaymentId(request.getPaymentId());

        for (ShopOrderRequest.ShopOrderItemRequest itemReq : request.getItems()) {
            ShopOrderItem item = new ShopOrderItem();
            item.setOrder(order);
            item.setShopItemId(itemReq.getShopItemId());
            item.setTitle(itemReq.getTitle());
            item.setType(itemReq.getType());
            item.setPrice(itemReq.getPrice());
            item.setQuantity(itemReq.getQuantity() != null ? itemReq.getQuantity() : 1);
            order.getItems().add(item);
        }

        order = shopOrderRepository.save(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", order.getId()));
    }
}
