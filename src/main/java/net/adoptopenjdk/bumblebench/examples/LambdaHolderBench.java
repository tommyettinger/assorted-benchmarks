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
 * LambdaHolderBench score: 1415684480.000000 (1.416G 2107.1%)
 *               uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LambdaHolderBench score: 3795466240.000000 (3.795G 2205.7%)
 *               uncertainty:   0.2%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LambdaHolderBench score: 3812410112.000000 (3.812G 2206.2%)
 *               uncertainty:   2.0%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LambdaHolderBench score: 4097697280.000000 (4.098G 2213.4%)
 *               uncertainty:   1.0%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LambdaHolderBench score: 3823270656.000000 (3.823G 2206.4%)
 *               uncertainty:   0.9%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LambdaHolderBench score: 4060556032.000000 (4.061G 2212.5%)
 *               uncertainty:   1.5%
 */
public final class LambdaHolderBench extends MicroBench {
	public static class IntHolder {
		public int value;
	}

	protected long doBatch(long numIterations) throws InterruptedException {
		IntHolder state = new IntHolder();
		java.util.function.IntSupplier sup = () -> ++state.value;
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
		{
			sum += sup.getAsInt();
		}
		return numIterations;
	}
}
