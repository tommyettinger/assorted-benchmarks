package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.MathUtils;
import ds.merry.IntMap;
import net.adoptopenjdk.bumblebench.core.MiniBench;

/**
 * Hotspot 13:
 * KJPGNoise_2D_Bench score: 19410046.000000 (19.41M 1678.1%)
 *                uncertainty:   1.6%
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
        final int halfIterations = MathUtils.nextPowerOfTwo((int)Math.sqrt(numIterationsPerLoop)) - 1;
        for (long i = 0; i < numLoops; i++) {
            final IntMap<IntMap<Object>> coll = new IntMap<>(16, 0.5f);
            int x = -halfIterations, y = -halfIterations;
            for (int j = 0; j < numIterationsPerLoop; j++) {
                startTimer();
                noise.noise2(x, y);
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