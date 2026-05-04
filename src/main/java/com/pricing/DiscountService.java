package com.pricing;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves discount-code percentages and applies VIP loyalty discount.
 */
public class DiscountService {

    private static final double VIP_DISCOUNT_RATE = 0.05;

    private static final Map<String, Double> CODE_RATES = new HashMap<>();
    static {
        CODE_RATES.put("SAVE10", 0.10);
        CODE_RATES.put("SAVE20", 0.20);
        CODE_RATES.put("SAVE30", 0.30);
    }

    /**
     * Returns the discount rate for a code, or 0.0 if unrecognised / null.
     */
    public double getCodeRate(String discountCode) {
        if (discountCode == null) return 0.0;
        return CODE_RATES.getOrDefault(discountCode, 0.0);
    }

    /**
     * Applies the code discount first, then the VIP loyalty discount on the
     * remaining amount.
     *
     * @return total discount amount
     */
    public double calculateDiscount(double subtotal, String discountCode, CustomerType customerType) {
        double codeDiscount = subtotal * getCodeRate(discountCode);
        double afterCode    = subtotal - codeDiscount;

        double vipDiscount = (customerType == CustomerType.VIP)
                ? afterCode * VIP_DISCOUNT_RATE
                : 0.0;

        return codeDiscount + vipDiscount;
    }
}
