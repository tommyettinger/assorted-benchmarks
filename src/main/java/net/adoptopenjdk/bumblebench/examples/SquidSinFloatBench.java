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

import com.badlogic.gdx.math.MathUtils;
import net.adoptopenjdk.bumblebench.core.MicroBench;
import squidpony.squidmath.NumberTools;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8 (AdoptOpenJDK):
 * <br>
 * SquidSinFloatBench score: 75530640.000000 (75.53M 1814.0%)
 *                uncertainty:   0.8%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SquidSinFloatBench score: 75560160.000000 (75.56M 1814.0%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SquidSinFloatBench score: 75913640.000000 (75.91M 1814.5%)
 *                uncertainty:   0.8%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * SquidSinFloatBench score: 75309200.000000 (75.31M 1813.7%)
 *                uncertainty:   0.6%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * SquidSinFloatBench score: 75510208.000000 (75.51M 1814.0%)
 *                uncertainty:   2.9%
 */
public final class SquidSinFloatBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = MathUtils.PI * 8f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= NumberTools.sin((sum + i) * shrink);
		return numIterations;
	}
}
