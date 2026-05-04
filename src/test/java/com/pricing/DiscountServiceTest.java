package com.pricing;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiscountService – Unit Tests")
class DiscountServiceTest {

    private DiscountService service;

    @BeforeEach void setUp() { service = new DiscountService(); }

    @Test void save10Rate()      { assertEquals(0.10, service.getCodeRate("SAVE10"), 0.001); }
    @Test void save20Rate()      { assertEquals(0.20, service.getCodeRate("SAVE20"), 0.001); }
    @Test void save30Rate()      { assertEquals(0.30, service.getCodeRate("SAVE30"), 0.001); }
    @Test void unknownCodeRate() { assertEquals(0.0,  service.getCodeRate("XYZ"),   0.001); }
    @Test void nullCodeRate()    { assertEquals(0.0,  service.getCodeRate(null),    0.001); }

    @Test @DisplayName("VIP extra discount is applied on top of code discount")
    void vipOnTopOfCode() {
        double d = service.calculateDiscount(100.0, "SAVE10", CustomerType.VIP);
        // code: 10, remaining 90, vip 5% of 90 = 4.5 → total 14.5
        assertEquals(14.5, d, 0.001);
    }

    @Test @DisplayName("REGULAR customer gets no VIP bonus")
    void noVipBonus() {
        double d = service.calculateDiscount(100.0, "SAVE10", CustomerType.REGULAR);
        assertEquals(10.0, d, 0.001);
    }
}
