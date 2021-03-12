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
 * This tests long generation even though Random is not very fast at it.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 8, HotSpot:
 * <br>
 * JavaRandomLongBench score: 58726960.000000 (58.73M 1788.8%)
 *                 uncertainty:   0.6%
 * <br>
 * With Java 15, OpenJ9, same hardware:
 * <br>
 * JavaRandomLongBench score: 59852804.000000 (59.85M 1790.7%)
 *                 uncertainty:   0.6%
 */
public final class JavaRandomLongBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		Random rng = new Random(0x12345678);
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
