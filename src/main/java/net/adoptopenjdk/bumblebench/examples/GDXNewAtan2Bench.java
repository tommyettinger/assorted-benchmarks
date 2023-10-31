package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

import static com.badlogic.gdx.math.MathUtils.PI;
import static com.github.tommyettinger.digital.TrigTools.HALF_PI;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * GDXNewAtan2Bench score: 79628464.000000 (79.63M 1819.3%)
 *              uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GDXNewAtan2Bench score: 61598568.000000 (61.60M 1793.6%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GDXNewAtan2Bench score: 78625560.000000 (78.63M 1818.0%)
 *              uncertainty:   0.4%
 * <br>
 * HotSpot Java 17:
 * <br>
 * GDXNewAtan2Bench score: 77566856.000000 (77.57M 1816.7%)
 *              uncertainty:   1.3%
 * <br>
 * Graal Java 17:
 * <br>
 * GDXNewAtan2Bench score: 85839768.000000 (85.84M 1826.8%)
 *              uncertainty:   0.6%
 */
public class GDXNewAtan2Bench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | ((int)(bits >> 40) & 0x807FFFFF));
    }
    /**
     * @param i any finite double or float, but more commonly a float
     * @return an output from the inverse tangent function, from {@code -HALF_PI} to {@code HALF_PI} inclusive */
    public static float atanUnchecked (double i) {
        double n = Math.abs(i);
        double c = (n - 1.0) / (n + 1.0);
        double c2 = c * c;
        double c3 = c * c2;
        double c5 = c3 * c2;
        double c7 = c5 * c2;
        double c9 = c7 * c2;
        double c11 = c9 * c2;
        // the difference is using Math.signum(i), which returns 0 if i is 0, instead of Math.copySign(), which doesn't.
        return (float)(Math.signum(i) * ((Math.PI * 0.25)
                + (0.99997726 * c - 0.33262347 * c3 + 0.19354346 * c5 - 0.11643287 * c7 + 0.05265332 * c9 - 0.0117212 * c11)));
    }

    /**
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, in radians as a float; ranges from {@code -PI} to {@code PI} */
    public static float atan2NewGDX (final float y, float x) {
        float n = y / x;
        if (n != n)
            n = (y == x ? 1f : -1f); // if both y and x are infinite, n would be NaN
        else if (n - n != n - n) x = 0f; // if n is infinite, y is infinitely larger than x.
        if (x > 0)
            return atanUnchecked(n);
        else if (x < 0) {
            if (y >= 0) return atanUnchecked(n) + PI;
            return atanUnchecked(n) - PI;
        } else if (y > 0)
            return x + HALF_PI;
        else if (y < 0) return x - HALF_PI;
        return x + y; // returns 0 for 0,0 or NaN if either y or x is NaN
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += MathUtils.atan2(nextExclusiveFloat(), nextExclusiveFloat());
        return numIterations;
    }
}
