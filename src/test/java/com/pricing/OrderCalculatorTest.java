package com.pricing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for the refactored {@link OrderCalculator}.
 */
@DisplayName("OrderCalculator – Refactored Tests")
class OrderCalculatorTest {

    private OrderCalculator calculator;

    @BeforeEach
    void setUp() { calculator = new OrderCalculator(); }

    // ── Subtotal ──────────────────────────────────────────────────────────────

    @Test @DisplayName("Single item subtotal is price × quantity")
    void subtotalSingleItem() {
        PricingResult r = calculator.calculate(List.of(50.0), List.of(3), CustomerType.REGULAR, null);
        assertEquals(150.0, r.getSubtotal(), 0.001);
    }

    @Test @DisplayName("Multiple items: subtotals summed")
    void subtotalMultipleItems() {
        PricingResult r = calculator.calculate(
                List.of(10.0, 20.0, 5.0), List.of(4, 2, 10), CustomerType.REGULAR, null);
        // 40 + 40 + 50 = 130
        assertEquals(130.0, r.getSubtotal(), 0.001);
    }

    // ── No-discount baseline ──────────────────────────────────────────────────

    @Test @DisplayName("Regular customer, no code: 0 discount, 19% tax")
    void regularNoCode() {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.REGULAR, null);
        assertEquals(0.0,   r.getDiscountAmount(), 0.001);
        assertEquals(19.0,  r.getTax(),            0.001);
        assertEquals(119.0, r.getFinalPrice(),     0.001);
    }

    // ── Discount codes ────────────────────────────────────────────────────────

    @ParameterizedTest(name = "Code {0} → discount {1}, final {2}")
    @CsvSource({
        "SAVE10, 10.0, 107.1",
        "SAVE20, 20.0,  95.2",
        "SAVE30, 30.0,  83.3"
    })
    void discountCodes(String code, double expectedDiscount, double expectedFinal) {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.REGULAR, code);
        assertEquals(expectedDiscount, r.getDiscountAmount(), 0.001);
        assertEquals(expectedFinal,    r.getFinalPrice(),     0.001);
    }

    @Test @DisplayName("Unknown code applies zero discount")
    void unknownCodeNoDiscount() {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.REGULAR, "BOGUS");
        assertEquals(0.0, r.getDiscountAmount(), 0.001);
    }

    @Test @DisplayName("Null code treated same as no code")
    void nullCodeNoDiscount() {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.REGULAR, null);
        assertEquals(0.0, r.getDiscountAmount(), 0.001);
    }

    // ── VIP ───────────────────────────────────────────────────────────────────

    @Test @DisplayName("VIP without code gets 5% loyalty discount")
    void vipLoyaltyOnly() {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.VIP, null);
        assertEquals(5.0,    r.getDiscountAmount(), 0.001);
        assertEquals(18.05,  r.getTax(),            0.001);
        assertEquals(113.05, r.getFinalPrice(),     0.001);
    }

    @Test @DisplayName("VIP + SAVE10 stacks discounts correctly")
    void vipPlusSave10() {
        PricingResult r = calculator.calculate(List.of(100.0), List.of(1), CustomerType.VIP, "SAVE10");
        // code: 10, remaining: 90, vip: 4.5 → total discount 14.5
        assertEquals(14.5,    r.getDiscountAmount(), 0.001);
        assertEquals(101.745, r.getFinalPrice(),     0.001);
    }

    // ── PricingResult components ──────────────────────────────────────────────

    @Test @DisplayName("Tax is 19% of (subtotal − discount)")
    void taxOnDiscountedAmount() {
        PricingResult r = calculator.calculate(List.of(200.0), List.of(1), CustomerType.REGULAR, "SAVE20");
        // subtotal=200, discount=40, taxable=160, tax=30.4
        assertEquals(30.4,  r.getTax(),        0.001);
        assertEquals(190.4, r.getFinalPrice(), 0.001);
    }

    @Test @DisplayName("FinalPrice = (subtotal − discount) + tax")
    void finalPriceEquation() {
        PricingResult r = calculator.calculate(List.of(150.0), List.of(2), CustomerType.VIP, "SAVE10");
        double expected = (r.getSubtotal() - r.getDiscountAmount()) + r.getTax();
        assertEquals(expected, r.getFinalPrice(), 0.001);
    }

    // ── Validation ────────────────────────────────────────────────────────────

    @Test @DisplayName("Mismatched list sizes throw IllegalArgumentException")
    void mismatchedListSizes() {
        assertThrows(IllegalArgumentException.class, () ->
            calculator.calculate(List.of(10.0, 20.0), List.of(1), CustomerType.REGULAR, null));
    }

    @Test @DisplayName("Empty lists throw IllegalArgumentException")
    void emptyLists() {
        assertThrows(IllegalArgumentException.class, () ->
            calculator.calculate(List.of(), List.of(), CustomerType.REGULAR, null));
    }

    @Test @DisplayName("Null prices throw IllegalArgumentException")
    void nullPrices() {
        assertThrows(IllegalArgumentException.class, () ->
            calculator.calculate(null, List.of(1), CustomerType.REGULAR, null));
    }
}
