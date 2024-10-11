package com.coupon;

import com.coupon.dto.CartItem;
import com.coupon.dto.CartRequest;
import com.coupon.dto.CouponRequest;
import com.coupon.entity.Coupon;
import com.coupon.repository.CouponRepository;
import com.coupon.service.impl.CouponServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @InjectMocks
    private CouponServiceImpl couponService;

    @Mock
    private CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplyCartWiseCoupon() {
        CartItem item1 = new CartItem(1L, 2, 100.0);
        CartItem item2 = new CartItem(2L, 1, 200.0);
        List<CartItem> items = List.of(item1, item2);
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItems(items);
        Map<String, Object> couponDetails = new HashMap<>();
        couponDetails.put("threshold", 250.0);
        couponDetails.put("discount", 10.0);
        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(couponDetails);
        Double discount = couponService.applyCartWiseCoupon(cartRequest, coupon);
        System.out.println(coupon);
        assertEquals(40.0, discount);
    }

    @Test
    void testApplyProductWiseCoupon() {
        CartItem item1 = new CartItem(1L, 3, 50.0);
        CartItem item2 = new CartItem(2L, 1, 100.0);
        List<CartItem> items = List.of(item1, item2);
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItems(items);
        Map<String, Object> couponDetails = new HashMap<>();
        couponDetails.put("product_id", 1L);
        couponDetails.put("discount", 20.0);
        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
        coupon.setDetails(couponDetails);
        Double discount = couponService.applyProductWiseCoupon(cartRequest, coupon);
        assertEquals(30.0, discount);
    }

    @Test
    void testApplyBxGyCoupon() {
        CartItem item1 = new CartItem(1L, 4, 50.0);
        CartItem item2 = new CartItem(2L, 2, 100.0);
        List<CartItem> items = List.of(item1, item2);
        CartRequest cartRequest = new CartRequest();
        cartRequest.setItems(items);
        Map<String, Object> buyProduct = new HashMap<>();
        buyProduct.put("product_id", 1L);
        buyProduct.put("quantity", 2);

        Map<String, Object> getProduct = new HashMap<>();
        getProduct.put("product_id", 2L);
        getProduct.put("quantity", 1);
        List<Map<String, Object>> buyProducts = List.of(buyProduct);
        List<Map<String, Object>> getProducts = List.of(getProduct);
        Map<String, Object> couponDetails = new HashMap<>();
        couponDetails.put("buy_products", buyProducts);
        couponDetails.put("get_products", getProducts);
        couponDetails.put("repetition_limit", 2);
        Coupon coupon = new Coupon();
        coupon.setType("bxgy");
        coupon.setDetails(couponDetails);
        Double discount = couponService.applyBxGyCoupon(cartRequest, coupon);
        assertEquals(200.0, discount);
    }

    @Test
    void testCreateCoupon() throws Exception {
        CouponRequest couponRequest = new CouponRequest();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> detailsMap = objectMapper.readValue("{\"threshold\": 100.0, \"discount\": 10.0}", Map.class);
        couponRequest.setType("cart-wise");
        couponRequest.setDetails(detailsMap);
        Coupon coupon = new Coupon();
        coupon.setType(couponRequest.getType());
        coupon.setDetails(detailsMap);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
        Coupon savedCoupon = couponService.createCoupon(couponRequest);
        assertNotNull(savedCoupon);
        assertEquals("cart-wise", savedCoupon.getType());
        assertEquals(detailsMap, savedCoupon.getDetails());
    }


    @Test
    void testGetAllCoupons() throws JsonProcessingException {

        List<Coupon> coupons = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        coupons.add(new Coupon(1L, "cart-wise", objectMapper.readValue("{\"threshold\": 100.0, \"discount\": 10.0}", Map.class)));
        coupons.add(new Coupon(2L, "product-wise", objectMapper.readValue("{\"product_id\": 1, \"discount\": 20.0}", Map.class)));
        when(couponRepository.findAll()).thenReturn(coupons);
        List<Coupon> result = couponService.getAllCoupons();
        assertEquals(2, result.size());
        assertEquals("cart-wise", result.get(0).getType());
    }

    @Test
    void testDeleteCoupon() {
        Coupon existingCoupon = new Coupon();
        existingCoupon.setId(1L);
        existingCoupon.setType("cart-wise");
        when(couponRepository.findById(1L)).thenReturn(Optional.of(existingCoupon));
        couponService.deleteCoupon(1L);
        verify(couponRepository).delete(existingCoupon);
    }

    @Test
    void testDeleteCoupon_CouponNotFound() {
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.deleteCoupon(1L);
        });
        String expectedMessage = "Coupon with ID 1 not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateCoupon() throws Exception {
        Coupon existingCoupon = new Coupon();
        existingCoupon.setId(1L);
        existingCoupon.setType("cart-wise");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonDetails = "{\"threshold\": 100.0, \"discount\": 15.0}";
        Map<String, Object> updatedDetailsMap = objectMapper.readValue(jsonDetails, Map.class);
        when(couponRepository.findById(1L)).thenReturn(Optional.of(existingCoupon));
        CouponRequest updateRequest = new CouponRequest();
        updateRequest.setType("cart-wise");
        updateRequest.setDetails(updatedDetailsMap);
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setId(1L);
        updatedCoupon.setType(updateRequest.getType());
        updatedCoupon.setDetails(updatedDetailsMap);
        when(couponRepository.save(any(Coupon.class))).thenReturn(updatedCoupon);
        Coupon result = couponService.updateCoupon(1L, updateRequest);
        verify(couponRepository).save(existingCoupon);
        assertNotNull(result);
        assertEquals("cart-wise", result.getType());
        assertEquals(updatedDetailsMap, result.getDetails());
    }


}

