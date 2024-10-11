package com.coupon.service.impl;

import com.coupon.dto.CartItem;
import com.coupon.dto.CartRequest;
import com.coupon.dto.CouponRequest;
import com.coupon.dto.CouponResponse;
import com.coupon.entity.Coupon;
import com.coupon.repository.CouponRepository;
import com.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Override
    public Coupon createCoupon(CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setType(request.getType());
        coupon.setDetails(request.getDetails());
        return couponRepository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon with ID " + id + " not found"));
    }

    @Override
    public Coupon updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon with ID " + id + " not found"));
        coupon.setType(request.getType());
        coupon.setDetails(request.getDetails());
        return couponRepository.save(coupon);
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Coupon with ID " + id + " not found"));
        couponRepository.delete(coupon);
    }


    @Override
    public List<CouponResponse> getApplicableCoupons(CartRequest cartRequest) {
        List<Coupon> allCoupons = couponRepository.findAll();
        List<CouponResponse> applicableCoupons = new ArrayList<>();
        for (Coupon coupon : allCoupons) {
            if (isCouponApplicable(cartRequest, coupon)) {
                Double discount = calculateDiscount(cartRequest, coupon);
                applicableCoupons.add(new CouponResponse(coupon.getId(), coupon.getType(), discount));
            }
        }

        return applicableCoupons;
    }

    @Override
    public CartRequest applyCoupon(Long couponId, CartRequest cartRequest) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        Double totalDiscount = calculateDiscount(cartRequest, coupon);
        Double cartTotal = calculateCartTotal(cartRequest.getItems());
        Double finalPrice = cartTotal - totalDiscount;

        cartRequest.setTotalPrice(cartTotal);
        cartRequest.setTotalDiscount(totalDiscount);
        cartRequest.setFinalPrice(finalPrice);

        return cartRequest;
    }

    private boolean isCouponApplicable(CartRequest cartRequest, Coupon coupon) {
        return switch (coupon.getType()) {
            case "cart-wise" -> applyCartWiseCoupon(cartRequest, coupon) > 0;
            case "product-wise" -> applyProductWiseCoupon(cartRequest, coupon) > 0;
            case "bxgy" -> applyBxGyCoupon(cartRequest, coupon) > 0;
            default -> false;
        };
    }

    private Double calculateDiscount(CartRequest cartRequest, Coupon coupon) {
        return switch (coupon.getType()) {
            case "cart-wise" -> applyCartWiseCoupon(cartRequest, coupon);
            case "product-wise" -> applyProductWiseCoupon(cartRequest, coupon);
            case "bxgy" -> applyBxGyCoupon(cartRequest, coupon);
            default -> 0.0;
        };
    }

    private Double calculateCartTotal(List<CartItem> items) {
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    private Double applyCartWiseCoupon(CartRequest cart, Coupon coupon) {
        Double cartTotal = calculateCartTotal(cart.getItems());
        Number thresholdNumber = (Number) coupon.getDetails().get("threshold");
        Number discountPercentNumber = (Number) coupon.getDetails().get("discount");
        Double threshold = thresholdNumber.doubleValue();
        Double discountPercent = discountPercentNumber.doubleValue();
        if (cartTotal > threshold) {
            return cartTotal * discountPercent / 100;
        }
        return 0.0;
    }

    private Double applyProductWiseCoupon(CartRequest cart, Coupon coupon) {
        double totalDiscount = 0.0;
        List<CartItem> items = cart.getItems();
        Number targetProductIdNumber = (Number) coupon.getDetails().get("product_id");
        Number discountPercentNumber = (Number) coupon.getDetails().get("discount");

        Long targetProductId = targetProductIdNumber.longValue();
        Double discountPercent = discountPercentNumber.doubleValue();

        for (CartItem item : items) {
            if (item.getId().equals(targetProductId)) {
                totalDiscount += (item.getQuantity() * item.getPrice()) * discountPercent / 100;
            }
        }

        return totalDiscount;
    }

    private Double applyBxGyCoupon(CartRequest cart, Coupon coupon) {
        List<Map<String, Object>> buyProducts = (List<Map<String, Object>>) coupon.getDetails().get("buy_products");
        List<Map<String, Object>> getProducts = (List<Map<String, Object>>) coupon.getDetails().get("get_products");
        Number repetitionLimitNumber = (Number) coupon.getDetails().getOrDefault("repetition_limit", Integer.MAX_VALUE);
        int repetitionLimit = repetitionLimitNumber.intValue();

        int buyCount = 0;
        double totalDiscount = 0.0;

        for (Map<String, Object> buyProduct : buyProducts) {
            Long buyProductId = ((Number) buyProduct.get("product_id")).longValue();
            Integer requiredQuantity = (Integer) buyProduct.get("quantity");

            for (CartItem item : cart.getItems()) {
                if (item.getId().equals(buyProductId)) {
                    buyCount += item.getQuantity() / requiredQuantity;
                }
            }
        }
        int applicableTimes = Math.min(buyCount, repetitionLimit);
        for (Map<String, Object> getProduct : getProducts) {
            Long getProductId = ((Number) getProduct.get("product_id")).longValue();
            Integer freeQuantity = (Integer) getProduct.get("quantity");

            for (CartItem item : cart.getItems()) {
                if (item.getId().equals(getProductId)) {
                    totalDiscount += Math.min(item.getQuantity(), applicableTimes * freeQuantity) * item.getPrice();
                }
            }
        }

        return totalDiscount;
    }


}
