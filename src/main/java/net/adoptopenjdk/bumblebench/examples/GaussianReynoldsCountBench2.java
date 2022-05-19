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

import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * GaussianReynoldsCountBench2 score: 455884032.000000 (455.9M 1993.8%)
 *                         uncertainty:   1.1%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GaussianReynoldsCountBench2 score: 373422784.000000 (373.4M 1973.8%)
 *                         uncertainty:   2.0%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GaussianReynoldsCountBench2 score: 476920928.000000 (476.9M 1998.3%)
 *                         uncertainty:   0.4%
 */
public final class GaussianReynoldsCountBench2 extends MicroBench {

	//// here we want to only request one long from rng.
	//// because the bitCount() doesn't really care about the numerical value of its argument, only its Hamming weight,
	//// we can do some very basic scrambling of the same random long and get the bit count of that.
	//// we use an XLCG for this purpose, with reversed order to preserve some desirable qualities of the long (if u is
	//// 0, then this returns 0.0, but it shouldn't return 0.0 for any other u value).
	//// 0xC6BC279692B5C323L is arbitrary, except that its last three bits need to be 011. Half of its bits are 1.
	//// 0xC6AC29E5C6AC29E5L is less arbitrary; it has a bit count of 32 as a whole, a bit count of 16 for both the
	//// upper and lower halves, and its last three bits are 101, a requirement of an XLCG.
	//// because it only needs one floating-point operation, it is quite fast on a CPU.
	//// this winds up being a very smooth Gaussian, as Marc B. Reynolds had it with two random longs.
	//// https://marc-b-reynolds.github.io/distribution/2021/03/18/CheapGaussianApprox.html
	private double nextGaussian(final LaserRandom rng){
		final long u = rng.nextLong();
		return 0x1.fb760cp-35 * ((Long.bitCount(u * 0xC6BC279692B5C323L ^ 0xC6AC29E5C6AC29E5L) - 32L << 32) + (u & 0xFFFFFFFFL) - (u >>> 32));
	}
	//// below is very fast, but has severe staircase patterns in the distribution.
//		return 0x1.fb760cp-35 * ((Long.bitCount(u ^ 0xC6BC279692B5C323L) - 32L << 32) + (u & 0xFFFFFFFFL) - (u >>> 32));
	//// below is probably fine, and could still be used.
//		return 0x1.fb760cp-35 * ((Long.bitCount((u ^ 0xD1342543DE82EF95L) * 0xC6BC279692B5C323L) - 32L << 32) + (u & 0xFFFFFFFFL) - (u >>> 32));

	protected long doBatch(long numIterations) throws InterruptedException {
		LaserRandom rng = new LaserRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += nextGaussian(rng);
		return numIterations;
	}
}
