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
 * GaussianReynoldsCountBench3 score: 497906080.000000 (497.9M 2002.6%)
 *                         uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GaussianReynoldsCountBench3 score: 395680768.000000 (395.7M 1979.6%)
 *                         uncertainty:   0.2%
 * <br>
 * HotSpot Java 16:
 * <br>
 * GaussianReynoldsCountBench3 score: 477663008.000000 (477.7M 1998.4%)
 *                         uncertainty:   1.0%
 */
public final class GaussianReynoldsCountBench3 extends MicroBench {

	//// here we want to only request one long from rng.
	//// because the bitCount() doesn't really care about the numerical value of its argument, only its Hamming weight,
	//// we use the random long un-scrambled, and get the bit count of that.
	//// for the later steps, we multiply the random long by a specific constant and get the difference of its halves.
	//// 0xC6AC29E4C6AC29E5L is... OK, it's complicated. It needs to have almost-identical upper and lower halves, but
	//// for reasons I don't currently understand, if the upper and lower halves are equal, then the min and max results
	//// of the Gaussian aren't equally distant from 0. By using an upper half that is exactly 1 less than the lower
	//// half, we get bounds of -7.929080009460449 to 7.929080009460449, returned when the RNG gives 0 and -1 resp.
	//// because it only needs one floating-point operation, it is quite fast on a CPU.
	//// this winds up being a very smooth Gaussian, as Marc B. Reynolds had it with two random longs.
	//// https://marc-b-reynolds.github.io/distribution/2021/03/18/CheapGaussianApprox.html
	private double nextGaussian(final LaserRandom rng){
		long u = rng.nextLong(), c = Long.bitCount(u) - 32L << 32;
		u *= 0xC6AC29E4C6AC29E5L;
		return 0x1.fb760cp-35 * (c + (u & 0xFFFFFFFFL) - (u >>> 32));
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
