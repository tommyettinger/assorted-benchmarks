package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;
import ds.merry.IntMap;
import net.adoptopenjdk.bumblebench.core.MiniBench;
import squidpony.squidmath.FastNoise;

/**
 * On HotSpot 13:
 * FastNoise_2D_Bench score: 21941390.000000 (21.94M 1690.4%)
 *                uncertainty:   0.7%
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
        final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
        for (long i = 0; i < numLoops; i++) {
            final IntMap<IntMap<Object>> coll = new IntMap<>(16, 0.5f);
            int x = -halfIterations, y = -halfIterations;
            for (int j = 0; j < numIterationsPerLoop; j++) {
                startTimer();
                noise.getConfiguredNoise(x, y);
                pauseTimer();
                if (++x > halfIterations) {
                    x = -halfIterations;
                    y++;
                }
            }
        }

        return numLoops * numIterationsPerLoop;
    }
}