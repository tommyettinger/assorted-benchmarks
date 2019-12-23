package net.adoptopenjdk.bumblebench.examples;

import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.squidmath.FastNoise;

/**
 * On HotSpot 13:
 * FastNoise_2D_Bench score: 21644908.000000 (21.64M 1689.0%)
 *                uncertainty:   1.6%
 * On OpenJ9 for Java 13:
 * FastNoise_2D_Bench score: 15165916.000000 (15.17M 1653.5%)
 *                uncertainty:   0.6%
 * Created by Tommy Ettinger on 12/23/2019.
 */
public class FastNoise_2D_Bench extends MiniBench {
    @Override
    protected int maxIterationsPerLoop() {
        return 100007;
    }

    @Override
    protected long doBatch(long numLoops, int numIterationsPerLoop) throws InterruptedException {
        FastNoise noise = FastNoise.instance;
        for (long i = 0; i < numLoops; i++) {
            for (int j = 0; j < numIterationsPerLoop; j++) {
                startTimer();
                noise.getConfiguredNoise(j & 0xAAAAAAAAAAAAAAAAL, j & 0x5555555555555555L);
                pauseTimer();
            }
        }
        return numLoops * numIterationsPerLoop;
    }
}