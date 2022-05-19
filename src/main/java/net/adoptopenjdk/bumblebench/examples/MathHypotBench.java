package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * MathHypotBench score: 2196056.500000 (2.196M 1460.2%)
 *            uncertainty:   3.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
 */
public class MathHypotBench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++) {
            long n = rng.nextLong();
            sum += Math.hypot((n & 0xFFFFL) - (n >>> 16 & 0xFFFFL), (n >>> 32 & 0xFFFFL) - (n >>> 48));
        }
        return numIterations;
    }
}
