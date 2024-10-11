package com.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CouponResponse {
    private Long couponId;
    private String type;
    private Double discount;
}
