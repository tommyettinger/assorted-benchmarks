package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;
import com.github.tommyettinger.ds.support.BitConversion;
import com.github.tommyettinger.ds.support.TricycleRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * GDXAtan2Bench score: 87204168.000000 (87.20M 1828.4%)
 *           uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GDXAtan2Bench score: 67759704.000000 (67.76M 1803.1%)
 *           uncertainty:   2.1%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GDXAtan2Bench score: 84753784.000000 (84.75M 1825.5%)
 *           uncertainty:   1.9%
 */
public class GDXAtan2Bench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return BitConversion.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | (int)(bits >> 41 & 0x807FFFFF));
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += MathUtils.atan2(nextExclusiveFloat(), nextExclusiveFloat());
        return numIterations;
    }
}
