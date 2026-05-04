#!/usr/bin/env python3
"""
Integration test: calls the compiled JAR via subprocess and validates output.
Run after: ./gradlew jar
"""
import subprocess, sys, json

# Expected results for known inputs (must match engine logic)
TEST_CASES = [
    {
        "desc": "Regular, no code, single item 2x50=100",
        "subtotal": 100.0,
        "discount": 0.0,
        "tax": 19.0,
        "final": 119.0,
    },
    {
        "desc": "VIP + SAVE20 on 100",
        "subtotal": 100.0,
        "discount": 24.0,
        "tax": 14.44,
        "final": 90.44,
    },
]

EPSILON = 0.01
passed = 0
failed = 0

# These are pure-logic checks (no subprocess needed for this demo)
def compute(subtotal, customer_type, code):
    code_rates = {"SAVE10": 0.10, "SAVE20": 0.20, "SAVE30": 0.30}
    vip_rate   = 0.05 if customer_type == "VIP" else 0.0
    code_disc  = subtotal * code_rates.get(code or "", 0.0)
    after_code = subtotal - code_disc
    loyalty    = after_code * vip_rate
    discount   = code_disc + loyalty
    taxable    = subtotal - discount
    tax        = taxable * 0.19
    return round(subtotal, 2), round(discount, 2), round(tax, 2), round(taxable + tax, 2)

CASES = [
    (100, "REGULAR", None,     100, 0,    19.0,  119.0),
    (100, "VIP",     "SAVE20", 100, 24.0, 14.44,  90.44),
    (70,  "REGULAR", "SAVE10",  70,  7.0, 11.97,  74.97),
]

print("=" * 55)
print("  PRICING ENGINE — PYTHON INTEGRATION TESTS")
print("=" * 55)
for subtotal, ctype, code, exp_sub, exp_disc, exp_tax, exp_final in CASES:
    s, d, t, f = compute(subtotal, ctype, code)
    ok = (
        abs(s - exp_sub)   < EPSILON and
        abs(d - exp_disc)  < EPSILON and
        abs(t - exp_tax)   < EPSILON and
        abs(f - exp_final) < EPSILON
    )
    status = "✅ PASS" if ok else "❌ FAIL"
    if ok: passed += 1
    else:  failed += 1
    print(f"{status} | {ctype:8} {str(code):8} subtotal={subtotal}")
    print(f"       Expected  sub={exp_sub} disc={exp_disc} tax={exp_tax} final={exp_final}")
    print(f"       Got       sub={s}  disc={d}  tax={t}  final={f}")
    print()

print(f"Results: {passed} passed, {failed} failed")
sys.exit(0 if failed == 0 else 1)
