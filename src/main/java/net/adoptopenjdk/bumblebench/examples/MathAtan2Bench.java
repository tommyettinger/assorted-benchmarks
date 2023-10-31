package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MathAtan2Bench score: 16367868.000000 (16.37M 1661.1%)
 *            uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathAtan2Bench score: 16768764.000000 (16.77M 1663.5%)
 *            uncertainty:   1.3%
 * <br>
 * HotSpot Java 16:
 * <br>
 * MathAtan2Bench score: 15897182.000000 (15.90M 1658.2%)
 *            uncertainty:   1.0%
 */
public class MathAtan2Bench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | ((int)(bits >> 40) & 0x807FFFFF));
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        double sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += Math.atan2(nextExclusiveFloat(), nextExclusiveFloat());
        return numIterations;
    }
}
