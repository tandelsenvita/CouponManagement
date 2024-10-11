package com.coupon.service;

import com.coupon.dto.CartItem;
import com.coupon.dto.CartRequest;
import com.coupon.dto.CouponRequest;
import com.coupon.dto.CouponResponse;
import com.coupon.entity.Coupon;

import java.util.List;

public interface CouponService {
    Coupon createCoupon(CouponRequest request);

    List<Coupon> getAllCoupons();

    Coupon getCouponById(Long id);

    Coupon updateCoupon(Long id, CouponRequest request);

    void deleteCoupon(Long id);

    List<CouponResponse> getApplicableCoupons(CartRequest cartRequest);

    CartRequest applyCoupon(Long couponId, CartRequest cartRequest);
}
