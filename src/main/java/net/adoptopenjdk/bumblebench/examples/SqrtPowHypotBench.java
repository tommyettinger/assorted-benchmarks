package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SqrtPowHypotBench score: 117856160.000000 (117.9M 1858.5%)
 *               uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
 */
public class SqrtPowHypotBench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private static float distance(float x, float y){
        return (float) Math.sqrt(Math.pow(x, 2f) + Math.pow(y, 2f));
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
