package com.coupon.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CouponRequest {
    private String type;
    private Map<String, Object> details;
}
