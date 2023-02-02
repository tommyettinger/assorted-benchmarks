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
import com.github.tommyettinger.digital.TrigTools;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * DigitalSinBench score: 149810848.000000 (149.8M 1882.5%)
 *             uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * DigitalSinBench score: 149791632.000000 (149.8M 1882.5%)
 *             uncertainty:   0.4%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * DigitalSinBench score: 151432256.000000 (151.4M 1883.6%)
 *             uncertainty:   0.5%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * DigitalSinBench score: 149509440.000000 (149.5M 1882.3%)
 *             uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * DigitalSinBench score: 150604880.000000 (150.6M 1883.0%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * DigitalSinBench score: 149607872.000000 (149.6M 1882.4%)
 *             uncertainty:   0.3%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * DigitalSinBench score: 149943920.000000 (149.9M 1882.6%)
 *             uncertainty:   0.4%
 */
public final class DigitalSinBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = MathUtils.PI * 8f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= TrigTools.sin((sum + i) * shrink);
		return numIterations;
	}
}

/*
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
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 */
