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
 * HotSpot Java 8:
 * <br>
 * JavaRandomLongBench score: 61803784.000000 (61.80M 1793.9%)
 *                 uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * JavaRandomLongBench score: 61998444.000000 (62.00M 1794.3%)
 *                 uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * JavaRandomLongBench score: 62048896.000000 (62.05M 1794.3%)
 *                 uncertainty:   1.9%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * JavaRandomLongBench score: 63836576.000000 (63.84M 1797.2%)
 *                 uncertainty:   0.2%
 * <br>
 * GraalVM Java 17:
 * <br>
 * JavaRandomLongBench score: 403292800.000000 (403.3M 1981.5%)
 *                 uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * JavaRandomLongBench score: 61527740.000000 (61.53M 1793.5%)
 *                 uncertainty:   0.5%
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * JavaRandomLongBench score: 63727604.000000 (63.73M 1797.0%)
 *                 uncertainty:   0.6%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * JavaRandomLongBench score: 63558280.000000 (63.56M 1796.7%)
 *                 uncertainty:   0.2%
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

// Old results
/*
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
 * <br>
 * HotSpot Java 16:
 * <br>
 * JavaRandomLongBench score: 58880216.000000 (58.88M 1789.1%)
 *                 uncertainty:   0.3%
 */
