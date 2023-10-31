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
 * LambdaArrayBench score: 1408102272.000000 (1.408G 2106.6%)
 *              uncertainty:   3.7%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LambdaArrayBench score: 3763616512.000000 (3.764G 2204.9%)
 *              uncertainty:   0.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LambdaArrayBench score: 3875912192.000000 (3.876G 2207.8%)
 *              uncertainty:   0.3%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LambdaArrayBench score: 4017614848.000000 (4.018G 2211.4%)
 *              uncertainty:   1.4%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LambdaArrayBench score: 3837356544.000000 (3.837G 2206.8%)
 *              uncertainty:   0.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LambdaArrayBench score: 4045534720.000000 (4.046G 2212.1%)
 *              uncertainty:   0.4%
 */
public final class LambdaArrayBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		int[] state = new int[1];
		java.util.function.IntSupplier sup = () -> ++state[0];
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
		{
			sum += sup.getAsInt();
		}
		return numIterations;
	}
}
