package com.coupon.dto;

import lombok.Data;

@Data
@AllArgsConstructor
public class CartItem {
    private Long id;
    private Integer quantity;
    private Double price;
}
