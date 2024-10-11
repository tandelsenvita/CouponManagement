package com.coupon.controller;

import com.coupon.dto.CartRequest;
import com.coupon.dto.CouponRequest;
import com.coupon.dto.CouponResponse;
import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody CouponRequest request) {
        Coupon coupon = couponService.createCoupon(request);
        return ResponseEntity.ok(coupon);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/applicable-coupons")
    public ResponseEntity<List<CouponResponse>> getApplicableCoupons(@RequestBody CartRequest cartRequest) {
        List<CouponResponse> applicableCoupons = couponService.getApplicableCoupons(cartRequest);
        return ResponseEntity.ok(applicableCoupons);
    }

    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<CartRequest> applyCoupon(@PathVariable Long id, @RequestBody CartRequest cartRequest) {
        CartRequest updatedCart = couponService.applyCoupon(id, cartRequest);
        return ResponseEntity.ok(updatedCart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest request) {
        Coupon updatedCoupon = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(updatedCoupon);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }



}



