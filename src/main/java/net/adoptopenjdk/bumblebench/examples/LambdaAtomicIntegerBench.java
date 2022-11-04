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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * LambdaAtomicIntegerBench score: 173262784.000000 (173.3M 1897.0%)
 *                      uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * LambdaAtomicIntegerBench score: 218811440.000000 (218.8M 1920.4%)
 *                      uncertainty:   1.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * LambdaAtomicIntegerBench score: 202151744.000000 (202.2M 1912.5%)
 *                      uncertainty:   0.6%
 * <br>
 * GraalVM Java 17:
 * <br>
 * LambdaAtomicIntegerBench score: 236180336.000000 (236.2M 1928.0%)
 *                      uncertainty:   0.7
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * LambdaAtomicIntegerBench score: 202521680.000000 (202.5M 1912.6%)
 *                      uncertainty:   0.5%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * LambdaAtomicIntegerBench score: 203403936.000000 (203.4M 1913.1%)
 *                      uncertainty:   0.4%
 */
public final class LambdaAtomicIntegerBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		AtomicInteger state = new AtomicInteger(0);
		java.util.function.IntSupplier sup = state::incrementAndGet;
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
		{
			sum += sup.getAsInt();
		}
		return numIterations;
	}
}
