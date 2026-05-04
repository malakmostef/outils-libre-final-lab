"""
Integration tests for the Pricing Engine.

Requires the JAR to be built first:
    ./gradlew jar
Then run with:
    python3 src/integration/test_pricing_engine.py
"""

import subprocess
import sys
import os
import unittest

JAR_PATH = os.path.join(os.path.dirname(__file__), "../../build/libs/pricing-engine-1.0.0.jar")
RUNNER   = os.path.join(os.path.dirname(__file__), "PricingRunner.java")


def _run_java(prices: list, quantities: list, customer_type: str, code: str | None) -> dict:
    """Compile & run PricingRunner and return a parsed result dict."""
    prices_arg    = ",".join(map(str, prices))
    qty_arg       = ",".join(map(str, quantities))
    code_arg      = code if code else "NONE"

    cmd = [
        "java", "-cp", JAR_PATH,
        "com.pricing.IntegrationRunner",
        prices_arg, qty_arg, customer_type, code_arg
    ]
    result = subprocess.run(cmd, capture_output=True, text=True, timeout=15)
    if result.returncode != 0:
        raise RuntimeError(f"Java process failed:\n{result.stderr}")

    lines = result.stdout.strip().splitlines()
    data  = {}
    for line in lines:
        key, _, value = line.partition("=")
        data[key.strip()] = float(value.strip())
    return data


class PricingIntegrationTest(unittest.TestCase):

    def test_regular_no_discount(self):
        r = _run_java([100.0], [1], "REGULAR", None)
        self.assertAlmostEqual(r["subtotal"],  100.0, places=2)
        self.assertAlmostEqual(r["discount"],    0.0, places=2)
        self.assertAlmostEqual(r["final"],     119.0, places=2)

    def test_save20_regular(self):
        r = _run_java([200.0], [1], "REGULAR", "SAVE20")
        self.assertAlmostEqual(r["discount"],   40.0, places=2)
        self.assertAlmostEqual(r["final"],     190.4, places=2)

    def test_vip_save10(self):
        r = _run_java([100.0], [1], "VIP", "SAVE10")
        self.assertAlmostEqual(r["discount"],  14.5,   places=2)
        self.assertAlmostEqual(r["final"],    101.745, places=3)

    def test_multiple_items(self):
        r = _run_java([50.0, 30.0], [2, 3], "REGULAR", None)
        # subtotal = 100 + 90 = 190
        self.assertAlmostEqual(r["subtotal"], 190.0, places=2)
        self.assertAlmostEqual(r["final"],    226.1, places=2)


if __name__ == "__main__":
    if not os.path.exists(JAR_PATH):
        print(f"[SKIP] JAR not found at {JAR_PATH}. Run './gradlew jar' first.")
        sys.exit(0)
    unittest.main(verbosity=2)
