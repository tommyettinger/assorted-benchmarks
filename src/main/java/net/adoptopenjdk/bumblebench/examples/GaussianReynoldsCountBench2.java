/*******************************************************************************
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import com.github.tommyettinger.ds.support.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 *
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16:
 * <br>
 *
 */
public final class GaussianReynoldsCountBench2 extends MicroBench {

	private double nextGaussian(final LaserRandom rng){
		final long u1 = rng.nextLong();
		return 0x1.fb760cp-35 * ((Long.bitCount((u1 ^ 0xD1342543DE82EF95L) * 0xC6BC279692B5C323L) - 32L << 32) + (u1 & 0xFFFFFFFFL) - (u1 >>> 32));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextGaussian(rng);
		return numIterations;
	}
}
