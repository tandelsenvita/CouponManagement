package com.coupon.dto;

import lombok.Data;

import java.util.List;
@Data
public class CartRequest {
    private List<CartItem> items;
    private Double totalPrice;
    private Double totalDiscount;
    private Double finalPrice;
}
