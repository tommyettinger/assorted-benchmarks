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

import java.util.Random;

/**
 * This tests int generation instead of long generation, because Random is handicapped on longs.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 8, HotSpot:
 * <br>
 * JavaRandomBench score: 117254840.000000 (117.3M 1858.0%)
 *             uncertainty:   0.4%
 * <br>
 * With Java 15, OpenJ9, same hardware:
 * <br>
 * JavaRandomBench score: 119695936.000000 (119.7M 1860.0%)
 *             uncertainty:   0.2%
 */
public final class JavaRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Random rng = new Random(0x12345678);
		int sum = 0;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextInt();
		return numIterations;
	}
}
