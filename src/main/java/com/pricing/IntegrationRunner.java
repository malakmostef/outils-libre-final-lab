package com.pricing;

import java.util.ArrayList;
import java.util.List;

/**
 * CLI entry-point used ONLY by Python integration tests.
 *
 * Usage:  java com.pricing.IntegrationRunner <prices> <quantities> <customerType> <discountCode|NONE>
 * Output: one key=value per line
 */
public class IntegrationRunner {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: IntegrationRunner prices qty customerType code");
            System.exit(1);
        }

        List<Double>  prices     = parse(args[0]);
        List<Integer> quantities = parseInts(args[1]);
        CustomerType  type       = CustomerType.valueOf(args[2]);
        String        code       = "NONE".equals(args[3]) ? null : args[3];

        PricingResult r = new OrderCalculator().calculate(prices, quantities, type, code);

        System.out.println("subtotal="  + r.getSubtotal());
        System.out.println("discount="  + r.getDiscountAmount());
        System.out.println("tax="       + r.getTax());
        System.out.println("final="     + r.getFinalPrice());
    }

    private static List<Double> parse(String s) {
        List<Double> list = new ArrayList<>();
        for (String v : s.split(",")) list.add(Double.parseDouble(v));
        return list;
    }

    private static List<Integer> parseInts(String s) {
        List<Integer> list = new ArrayList<>();
        for (String v : s.split(",")) list.add(Integer.parseInt(v));
        return list;
    }
}
