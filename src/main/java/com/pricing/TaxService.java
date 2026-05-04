package com.pricing;

/**
 * Computes tax on a taxable amount.
 */
public class TaxService {

    private static final double TAX_RATE = 0.19;   // 19 % VAT

    public double calculateTax(double taxableAmount) {
        return taxableAmount * TAX_RATE;
    }
}
