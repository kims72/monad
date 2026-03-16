package spelbergit.math

import org.junit.jupiter.api.Test
import kotlin.math.abs

// Unit tests for toFracture function
class RationalTest {

    @Test
    fun testIntegerValues() {
        assert(Rational.toFracture(5.0) == Fracture(5, 1))
        assert(Rational.toFracture(0.0) == Fracture(0, 1))
        assert(Rational.toFracture(-3.0) == Fracture(-3, 1))
    }

    @Test
    fun testSimpleFractions() {
        assert(Rational.toFracture(0.5) == Fracture(1, 2))
        assert(Rational.toFracture(0.25) == Fracture(1, 4))
        assert(Rational.toFracture(0.75) == Fracture(3, 4))
        assert(Rational.toFracture(0.333333) == Fracture(1, 3))
    }

    @Test
    fun testNegativeFractions() {
        assert(Rational.toFracture(-0.5) == Fracture(-1, 2))
        assert(Rational.toFracture(-0.25) == Fracture(-1, 4))
        assert(Rational.toFracture(-1.5) == Fracture(-3, 2))
    }

    @Test
    fun testComplexFractions() {
        val piApprox = Rational.toFracture(3.14159265359)
        assert(abs(piApprox.numerator.toDouble() / piApprox.denominator - 3.14159265359) < 0.0001)

        val eApprox = Rational.toFracture(2.71828)
        assert(abs(eApprox.numerator.toDouble() / eApprox.denominator - 2.71828) < 0.0001)
    }

    @Test
    fun testMaxDenominator() {
        val fracture = Rational.toFracture(3.14159265359, 100)
        assert(fracture.denominator <= 100)
    }

    @Test
    fun testInvalidInputs() {
        try {
            Rational.toFracture(Double.NaN)
            assert(false) { "Should throw IllegalArgumentException for NaN" }
        } catch (e: IllegalArgumentException) {
            // Expected
        }

        try {
            Rational.toFracture(Double.POSITIVE_INFINITY)
            assert(false) { "Should throw IllegalArgumentException for infinity" }
        } catch (e: IllegalArgumentException) {
            // Expected
        }

        try {
            Rational.toFracture(Double.NEGATIVE_INFINITY)
            assert(false) { "Should throw IllegalArgumentException for negative infinity" }
        } catch (e: IllegalArgumentException) {
            // Expected
        }
    }
}