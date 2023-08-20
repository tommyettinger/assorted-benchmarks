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

import com.github.tommyettinger.digital.MathTools;
import com.github.tommyettinger.random.WhiskerRandom;
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
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * GaussianLogitBench score: 94903432.000000 (94.90M 1836.8%)
 *                uncertainty:   0.2%
 */
public final class GaussianLogitBench extends MicroBench {
	/**
	 * Meant to imitate {@link MathTools#probit(double)} using the simpler logit function. This scales the actual logit
	 * function by {@code Math.sqrt(Math.PI/8.0)}, which makes it have the same slope as probit when x is 0.5. The
	 * permissible values for x are between 0.0 and 1.0 inclusive. If you pass 0, you will get negative infinity, and if
	 * you pass 1, you will get positive infinity.
	 * @param x between 0 and 1, inclusive if you do accept infinite outputs, or exclusive if you do not
	 * @return an approximately-normal-distributed double with mu = 0.0, sigma = 1.0
	 */
	public static double logit(double x) {
		return 0.6266570686577501 * Math.log(x / (1.0 - x));
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		WhiskerRandom rng = new WhiskerRandom(0x12345678);
		double sum = 0.0;
		for (long i = 0; i < numIterations; i++)
			sum += logit(rng.nextExclusiveDouble());
		return numIterations;
	}
}
