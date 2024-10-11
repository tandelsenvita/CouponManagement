# Implemented Cases
====================================
1. Cart-wise Discount Coupons
Description: Discounts are applied based on the total value of the cart. If the cart value exceeds a specified threshold, a percentage discount is applied to the total cart value.
Implementation:
The coupon checks if the cart total exceeds a predefined threshold. If so, the discount percentage is applied.
Example:
Coupon details: { "type": "cart-wise", "threshold": 100.0, "discount": 10.0 }
If the cart total is $120, a 10% discount ($12) will be applied, resulting in a final total of $108.
2. Product-wise Discount Coupons
Description: Discounts are applied to specific products in the cart if they are eligible for the coupon. The discount is based on a percentage applied to the product price.
Implementation:
The coupon targets a specific product ID in the cart and applies the discount percentage to the product's price.
Example:
Coupon details: { "type": "product-wise", "product_id": 101, "discount": 15.0 }
If the product with ID 101 is in the cart and costs $50, a 15% discount ($7.50) will be applied to that product.
3. BxGy (Buy X Get Y) Coupons
Description: Customers receive free products or discounts on products when they buy a specified quantity of certain items.
Implementation:
The coupon defines a set of products to be purchased and a set of products to be given free (or discounted). The number of free products is determined based on the number of times the purchase requirement is met (limited by a repetition limit).
Example:
Coupon details:
json
Copy code
{
  "type": "bxgy",
  "buy_products": [{ "product_id": 101, "quantity": 2 }],
  "get_products": [{ "product_id": 102, "quantity": 1 }],
  "repetition_limit": 3
}
If the customer buys 4 units of product 101, they will receive 2 units of product 102 for free (since 4/2 = 2 repetitions, which is within the repetition limit).

# Unimplemented Cases
1.Coupon Stacking:
Description: The ability to apply multiple coupons to the same cart.
Reason: Not implemented due to potential complexity in determining interaction rules between coupons (e.g., stacking product-wise and cart-wise discounts). Implementing this feature requires advanced prioritization and interaction logic for coupon application order.
2.Complex Constraints on Coupons:
Description: Coupons with more complex constraints, such as time-limited or customer-specific coupons.
Reason: These were considered but not implemented in this version to focus on the core functionalities of discount application.
3.Coupons with Multiple Product Categories:
Description: Support for category-wide discounts (e.g., discounts on all electronics).
Reason: Handling category-wide discounts would require additional logic to link products with categories and process discounts accordingly, which was outside the scope of this implementation.

# Limitations
1. Fixed Data Types for Coupon Details:
The details field is stored as a JSON string and deserialized into a Map<String, Object>. This implementation assumes correct data types are provided when creating a coupon. Any mismatch (e.g., providing an Integer where a Double is expected) could cause runtime exceptions.

3. No Validation for Coupon Expiry:
Although the system supports an expiration date for coupons, there is no automatic validation or exclusion of expired coupons when applying them. Expiry validation needs to be added.

3.Single Coupon Application:
Currently, only one coupon can be applied to a cart at a time. The system doesn't support multiple coupons for a single cart, meaning users cannot combine discounts.

4. Simple Buy X Get Y Implementation:
The BxGy coupon implementation does not handle partial matches (e.g., buying less than the required quantity for partial discounts) and assumes integer-based repetition limits.

# Assumptions
1.Coupon Data Consistency:
It is assumed that the data provided when creating coupons, especially in the details field, is valid and consistent. For example, when applying a cart-wise coupon, it is assumed that the threshold and discount fields are both valid Double values.

2.Repetition Limit for BxGy Coupons:
For BxGy coupons, it is assumed that the repetition limit is always provided and valid. If it is missing or null, the system defaults to an effectively unlimited repetition limit (using Integer.MAX_VALUE).

3.Valid Coupon Types:
Only the following coupon types are supported: cart-wise, product-wise, and bxgy. Any other coupon type passed will be considered invalid.

4.Product-Specific Discounts:
For product-wise coupons, it is assumed that the product ID provided matches the products in the cart. The system doesn't validate product existence outside of the cart context.
