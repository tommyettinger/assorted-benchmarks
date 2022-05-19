package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SqrtHypotBench score: 422491872.000000 (422.5M 1986.2%)
 *            uncertainty:   2.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
 */
public class SqrtHypotBench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private static float distance(float x, float y){
        return (float) Math.sqrt(x * x + y * y);
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++) {
            long n = rng.nextLong();
            sum += distance((n & 0xFFFFL) - (n >>> 16 & 0xFFFFL), (n >>> 32 & 0xFFFFL) - (n >>> 48));
        }
        return numIterations;
    }
}
