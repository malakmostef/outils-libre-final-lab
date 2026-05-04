package com.pricing;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaxService – Unit Tests")
class TaxServiceTest {

    private TaxService service;

    @BeforeEach void setUp() { service = new TaxService(); }

    @Test void taxOnHundred()  { assertEquals(19.0, service.calculateTax(100.0), 0.001); }
    @Test void taxOnZero()     { assertEquals(0.0,  service.calculateTax(0.0),   0.001); }
    @Test void taxRoundTrip()  { assertEquals(9.5,  service.calculateTax(50.0),  0.001); }
}
