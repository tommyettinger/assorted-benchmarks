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

import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * PointHashXMixBench score: 250068384.000000 (250.1M 1933.7%)
 *                uncertainty:   0.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashXMixBench score: 246553696.000000 (246.6M 1932.3%)
 *                uncertainty:   0.5%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashXMixBench score: 243424688.000000 (243.4M 1931.0%)
 *                uncertainty:   3.6%
 */
public final class PointHashXMixBench extends MicroBench {

	private int hash(final int x, final int y){
		int r = x * 0x7C735 + y * 0x75915;
		r ^= (r << 25 | r >>> 7) ^ (r << 12 | r >>> 20);
		r = r * 0x7E57D + 0xCF019D85;
		return r ^ r >>> 16;
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + hash((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
