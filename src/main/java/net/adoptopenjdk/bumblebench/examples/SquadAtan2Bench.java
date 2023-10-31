package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.random.TricycleRandom;
import com.github.tommyettinger.digital.TrigTools;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SquadAtan2Bench score: 82002376.000000 (82.00M 1822.2%)
 *             uncertainty:  11.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SquadAtan2Bench score: 72722168.000000 (72.72M 1810.2%)
 *             uncertainty:   1.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * SquadAtan2Bench score: 97356888.000000 (97.36M 1839.4%)
 *             uncertainty:   1.0%
 */
public class SquadAtan2Bench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return Float.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | ((int)(bits >> 40) & 0x807FFFFF));
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += TrigTools.atan2(nextExclusiveFloat(), nextExclusiveFloat());
        return numIterations;
    }
}
