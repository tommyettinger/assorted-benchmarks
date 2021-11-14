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

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8 (AdoptOpenJDK):
 * <br>
 * GDXSinBench score: 150555696.000000 (150.6M 1883.0%)
 *         uncertainty:   0.9%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * GDXSinBench score: 151019840.000000 (151.0M 1883.3%)
 *         uncertainty:   0.3%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * GDXSinBench score: 150672832.000000 (150.7M 1883.1%)
 *         uncertainty:   1.2%
 * <br>
 * GraalVM CE Java 16:
 * <br>
 * GDXSinBench score: 149349824.000000 (149.3M 1882.2%)
 *         uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (SAP Machine):
 * <br>
 * GDXSinBench score: 148779184.000000 (148.8M 1881.8%)
 *         uncertainty:   0.6%
 */
public final class GDXSinBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = MathUtils.PI * 8f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= MathUtils.sin((sum + i) * shrink);
		return numIterations;
	}
}
