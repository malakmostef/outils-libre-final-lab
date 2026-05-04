package com.pricing;

import java.util.List;

/**
 * Orchestrates subtotal → discount → tax → final-price calculation.
 *
 * This is the refactored entry point; PricingEngine delegates here for
 * backward-compatibility.
 */
public class OrderCalculator {

    private final DiscountService discountService;
    private final TaxService      taxService;

    public OrderCalculator() {
        this(new DiscountService(), new TaxService());
    }

    /** Constructor for dependency injection (testing). */
    public OrderCalculator(DiscountService discountService, TaxService taxService) {
        this.discountService = discountService;
        this.taxService      = taxService;
    }

    /**
     * Calculates a full pricing breakdown.
     *
     * @param prices       unit prices (non-null, same size as quantities)
     * @param quantities   quantities  (non-null, same size as prices)
     * @param customerType REGULAR or VIP
     * @param discountCode optional discount code (may be null)
     * @return PricingResult containing every price component
     */
    public PricingResult calculate(List<Double>  prices,
                                   List<Integer> quantities,
                                   CustomerType  customerType,
                                   String        discountCode) {

        validateInputs(prices, quantities);

        double subtotal       = computeSubtotal(prices, quantities);
        double discountAmount = discountService.calculateDiscount(subtotal, discountCode, customerType);
        double taxableAmount  = subtotal - discountAmount;
        double tax            = taxService.calculateTax(taxableAmount);
        double finalPrice     = taxableAmount + tax;

        return new PricingResult(subtotal, discountAmount, tax, finalPrice);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private double computeSubtotal(List<Double> prices, List<Integer> quantities) {
        double subtotal = 0;
        for (int i = 0; i < prices.size(); i++) {
            subtotal += prices.get(i) * quantities.get(i);
        }
        return subtotal;
    }

    private void validateInputs(List<Double> prices, List<Integer> quantities) {
        if (prices == null || quantities == null)
            throw new IllegalArgumentException("Prices and quantities must not be null");
        if (prices.size() != quantities.size())
            throw new IllegalArgumentException("Prices and quantities must have the same size");
        if (prices.isEmpty())
            throw new IllegalArgumentException("Order must contain at least one item");
    }
}
