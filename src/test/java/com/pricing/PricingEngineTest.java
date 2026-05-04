package com.pricing;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Initial tests written against the original (bad-design) PricingEngine.
 * They document the EXPECTED behaviour before refactoring begins.
 */
@DisplayName("PricingEngine – Initial Tests")
class PricingEngineTest {

    private PricingEngine engine;

    @BeforeEach
    void setUp() { engine = new PricingEngine(); }

    // ── Subtotal ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Single item: price × quantity correct")
    void singleItemNoDiscount() {
        double result = engine.calc(List.of(100.0), List.of(1), "REGULAR", null);
        // subtotal=100, discount=0, tax=19, final=119
        assertEquals(119.0, result, 0.001);
    }

    @Test
    @DisplayName("Multiple items summed correctly")
    void multipleItemsNoDiscount() {
        double result = engine.calc(List.of(50.0, 30.0), List.of(2, 3), "REGULAR", null);
        // subtotal = 50*2 + 30*3 = 190, tax = 190*0.19 = 36.1, final = 226.1
        assertEquals(226.1, result, 0.001);
    }

    // ── Discount codes ────────────────────────────────────────────────────────

    @Test
    @DisplayName("SAVE10 applies 10% discount")
    void save10Discount() {
        double result = engine.calc(List.of(100.0), List.of(1), "REGULAR", "SAVE10");
        // subtotal=100, discount=10, after=90, tax=17.1, final=107.1
        assertEquals(107.1, result, 0.001);
    }

    @Test
    @DisplayName("SAVE20 applies 20% discount")
    void save20Discount() {
        double result = engine.calc(List.of(100.0), List.of(1), "REGULAR", "SAVE20");
        // subtotal=100, discount=20, after=80, tax=15.2, final=95.2
        assertEquals(95.2, result, 0.001);
    }

    @Test
    @DisplayName("SAVE30 applies 30% discount")
    void save30Discount() {
        double result = engine.calc(List.of(100.0), List.of(1), "REGULAR", "SAVE30");
        // after=70, tax=13.3, final=83.3
        assertEquals(83.3, result, 0.001);
    }

    @Test
    @DisplayName("Unknown discount code applies no discount")
    void unknownCode() {
        double result = engine.calc(List.of(100.0), List.of(1), "REGULAR", "BOGUS");
        assertEquals(119.0, result, 0.001);
    }

    // ── VIP customer ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("VIP customer gets extra 5% off after code discount")
    void vipNoCode() {
        double result = engine.calc(List.of(100.0), List.of(1), "VIP", null);
        // after vip: 100*0.95=95, tax=18.05, final=113.05
        assertEquals(113.05, result, 0.001);
    }

    @Test
    @DisplayName("VIP + SAVE10 stacked discount")
    void vipWithSave10() {
        double result = engine.calc(List.of(100.0), List.of(1), "VIP", "SAVE10");
        // after SAVE10: 90, after VIP 5%: 90*0.95=85.5, tax=16.245, final=101.745
        assertEquals(101.745, result, 0.001);
    }
}
