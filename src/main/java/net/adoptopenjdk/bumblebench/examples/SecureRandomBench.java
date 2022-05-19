
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

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * By algorithm:
 * <br>
 * "SHA1PRNG":
 * <br>
 * HotSpot Java 8:
 * <br>
 * SecureRandomBench score: 7926793.500000 (7.927M 1588.6%)
 *               uncertainty:   1.3%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SecureRandomBench score: 5186949.000000 (5.187M 1546.2%)
 *               uncertainty:   0.7%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SecureRandomBench score: 7452436.500000 (7.452M 1582.4%)
 *               uncertainty:   1.3%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SecureRandomBench score: 7255680.000000 (7.256M 1579.7%)
 *               uncertainty:   4.5%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SecureRandomBench score: 6668263.000000 (6.668M 1571.3%)
 *               uncertainty:   5.5%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SecureRandomBench score: 6016135.000000 (6.016M 1561.0%)
 *               uncertainty:   0.5%
 */
public final class SecureRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SecureRandom rng = null;
		try {
			rng = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
