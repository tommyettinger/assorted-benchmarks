package net.adoptopenjdk.bumblebench.examples;

import net.adoptopenjdk.bumblebench.core.MiniBench;

/**
 * Hotspot 13:
 * KJPGNoise_2D_Bench score: 21036286.000000 (21.04M 1686.2%)
 *                uncertainty:   0.5%
 * On OpenJ9 for Java 13:
 * KJPGNoise_2D_Bench score: 13221323.000000 (13.22M 1639.7%)
 *                uncertainty:   0.4%
 * Created by Tommy Ettinger on 12/23/2019.
 */
public class KJPGNoise_2D_Bench extends MiniBench {
    @Override
    protected int maxIterationsPerLoop() {
        return 100007;
    }

    @Override
    protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
        FastSimplexStyleNoise noise = new FastSimplexStyleNoise(123456789L);
        for (long i = 0; i < numLoops; i++) {
            for (int j = 0; j < numIterationsPerLoop; j++) {
                startTimer();
                noise.noise2(j & 0xAAAAAAAAAAAAAAAAL, j & 0x5555555555555555L);
                pauseTimer();
            }
        }
        return numLoops * numIterationsPerLoop;
    }
}