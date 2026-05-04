package com.pricing;

import java.util.List;

/**
 * Legacy façade kept for backward-compatibility.
 * Delegates all work to {@link OrderCalculator}.
 *
 * @deprecated Use {@link OrderCalculator} directly.
 */
@Deprecated
public class PricingEngine {

    private final OrderCalculator calculator = new OrderCalculator();

    public double calc(List<Double> prices, List<Integer> qty, String ctype, String code) {
        CustomerType customerType = CustomerType.valueOf(ctype);
        PricingResult result = calculator.calculate(prices, qty, customerType, code);
        System.out.println(result);
        return result.getFinalPrice();
    }
}
