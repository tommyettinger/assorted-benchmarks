package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.ds.support.BitConversion;
import com.github.tommyettinger.ds.support.TricycleRandom;
import com.github.yellowstonegames.core.TrigTools;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * SquadBadAtanBench score: 140382928.000000 (140.4M 1876.0%)
 *               uncertainty:   2.4%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SquadBadAtanBench score: 96825800.000000 (96.83M 1838.8%)
 *               uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * SquadBadAtanBench score: 131825224.000000 (131.8M 1869.7%)
 *               uncertainty:   0.4%
 */
public class SquadBadAtanBench extends MicroBench {
    private final TricycleRandom rng = new TricycleRandom(0x12345678);

    private float nextExclusiveFloat() {
        final long bits = rng.nextLong();
        return BitConversion.intBitsToFloat(126 - Long.numberOfTrailingZeros(bits) << 23
                | (int)(bits >> 41 & 0x807FFFFF));
    }

    protected long doBatch (long numIterations) throws InterruptedException {
        float sum = 0.1f;
        for (long i = 0; i < numIterations; i++)
            sum += TrigTools.atan(nextExclusiveFloat() / nextExclusiveFloat());
        return numIterations;
    }
}
