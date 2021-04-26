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
 * PointHashXLCGBench score: 248204128.000000 (248.2M 1933.0%)
 *                uncertainty:   0.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * PointHashXLCGBench score: 231604352.000000 (231.6M 1926.1%)
 *                uncertainty:   0.1%
 * <br>
 * HotSpot Java 16:
 * <br>
 * PointHashXLCGBench score: 244619024.000000 (244.6M 1931.5%)
 *                uncertainty:   0.4%
 */
public final class PointHashXLCGBench extends MicroBench {

	private int hash(final int x, final int y){
		final int n = (((x * 0x7C735) + (y * 0x75915) ^ 0xD1B54A35) * 0x9E373 ^ 0x91E10DA5) * 0x125493;
		return n ^ n >>> 16;
	}
	protected long doBatch(long numIterations) throws InterruptedException {
		long sum = 0x9ABCDEF012345678L;
		for (long i = 0; i < numIterations; i++) {
			sum = sum * 0xF7C2EBC08F67F2B5L + hash((int)sum, (int)(sum>>>32));
		}
		return numIterations;
	}
}
