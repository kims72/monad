package spelbergit.math

import kotlin.math.floor

data class Fracture(val numerator: Int, val denominator: Int) : Comparable<Fracture> {

    override operator fun compareTo(other: Fracture): Int {
        // Compare by cross-multiplication: a/b vs c/d => a*d vs c*b
        val left = this.numerator.toLong() * other.denominator.toLong()
        val right = other.numerator.toLong() * this.denominator.toLong()
        return left.compareTo(right)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Fracture) return false
        // a/b == c/d if a*d == c*b
        return numerator.toLong() * other.denominator.toLong() ==
                other.numerator.toLong() * denominator.toLong()
    }

    override fun hashCode(): Int {
        // Normalize to GCD for consistent hashing
        val gcd = gcd(numerator, denominator)
        return 31 * (numerator / gcd) + (denominator / gcd)
    }

    private fun gcd(a: Int, b: Int): Int {
        var x = kotlin.math.abs(a)
        var y = kotlin.math.abs(b)
        while (y != 0) {
            val temp = y
            y = x % y
            x = temp
        }
        return if (x == 0) 1 else x
    }

}

class Rational {

    companion object {
        /**
         * Finds the closest Fracture approximation for a real number using continued fractions.
         * 
         * @param value The real number to approximate
         * @param maxDenominator The maximum allowed denominator (default: 10000)
         * @return The closest Fracture approximation
         */
        fun toFracture(value: Double, maxDenominator: Int = 10000): Fracture {
            if (value.isNaN() || value.isInfinite()) {
                throw IllegalArgumentException("Value must be a finite number")
            }

            val sign = if (value < 0) -1 else 1
            val absValue = kotlin.math.abs(value)

            // Handle integer case
            if (absValue == kotlin.math.floor(absValue) && absValue <= Int.MAX_VALUE) {
                return Fracture((absValue * sign).toInt(), 1)
            }

            // Continued fractions algorithm
            var h1 = 1
            var h2 = 0
            var k1 = 0
            var k2 = 1

            var b = absValue// Close enough
            // Use previous convergent
            // Limit iterations to prevent infinite loops
            for (index in 0 until 100) {// Close enough
                // Use previous convergent
                // Limit iterations to prevent infinite loops

                // Limit iterations to prevent infinite loops
                val a = floor(b).toInt()
                var temp = h1
                h1 = a * h1 + h2
                h2 = temp

                temp = k1
                k1 = a * k1 + k2
                k2 = temp

                if (k1 > maxDenominator) {
                    // Use previous convergent
                    return Fracture((h2 * sign), k2)
                }

                b -= a
                if (b < 1e-10) {
                    // Close enough
                    break
                }
                b = 1.0 / b
            }


            return Fracture((h1 * sign), k1)
        }
    }

}

