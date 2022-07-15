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
 * MathLongFloorBench score: 16754.607422 (16.75K 972.6%)
 *                uncertainty:   2.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * MathLongFloorBench score: 15721.669922 (15.72K 966.3%)
 *                uncertainty:   1.8%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * MathLongFloorBench score: 162015.359375 (162.0K 1199.5%)
 *                uncertainty:   3.4%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * MathLongFloorBench score: 147390.296875 (147.4K 1190.1%)
 *                uncertainty:   2.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * MathLongFloorBench score: 155637.671875 (155.6K 1195.5%)
 *                uncertainty:   2.6%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * MathLongFloorBench score: 155729.406250 (155.7K 1195.6%)
 *                uncertainty:   5.5%
 */
public final class MathLongFloorBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		final int len = 10000;
		WhiskerRandom rng = new WhiskerRandom(0x12345678);
		double[] doubles = new double[len];
		long sum = 0;
		for (long i = 0; i < numIterations; i++) {
			pauseTimer();
			for (int j = 0; j < len; j++) {
				doubles[j] = (rng.nextExclusiveDouble() - 0.5) * 0x1p10;
			}
			startTimer();
			for (int j = 0; j < len; j++) {
				sum += (long)Math.floor(doubles[j]);
			}
		}
		return numIterations;
	}
}
