package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MathBadAtanBench score: 25393238.000000 (25.39M 1705.0%)
 *              uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathBadAtanBench score: 24753230.000000 (24.75M 1702.4%)
 *              uncertainty:   0.6%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathBadAtanBench score: 22913458.000000 (22.91M 1694.7%)
 *              uncertainty:   1.7%
 * <br>
 * This is a "bad atan()" benchmark because it tests using {@code atan(y/x)} when {@code atan2(y, x)} is more
 * appropriate, especially because atan2() handles all quadrants.
 */
public class MathBadAtanBench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | ((int)(bits >> 40) & 0x807FFFFF));
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        double sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += Math.atan(nextExclusiveFloat() / nextExclusiveFloat());
        return numIterations;
    }
}
