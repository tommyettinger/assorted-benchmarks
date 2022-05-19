
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
import java.security.SecureRandom;

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
 * <br>
 * "NativePRNGBlocking" is not available on any of these.
 * <br>
 * "NativePRNGNonBlocking" is not available on any of these.
 * <br>
 * "PKCS11" is not available on any of these.
 * <br>
 * "Windows-PRNG":
 * <br>
 * HotSpot Java 8:
 * <br>
 * SecureRandomBench score: 10399.057617 (10.40K 924.9%)
 *               uncertainty:   3.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * SecureRandomBench score: 6091362.000000 (6.091M 1562.2%)
 *               uncertainty:   0.6%
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * SecureRandomBench score: 2387416.250000 (2.387M 1468.6%)
 *               uncertainty:   1.8%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * SecureRandomBench score: 2660124.000000 (2.660M 1479.4%)
 *               uncertainty:   0.8%
 * <br>
 * GraalVM Java 17:
 * <br>
 * SecureRandomBench score: 2825773.750000 (2.826M 1485.4%)
 *               uncertainty:   2.2%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 * SecureRandomBench score: 5732210.000000 (5.732M 1556.2%)
 *               uncertainty:   0.1%
 */
public final class SecureRandomBench extends MicroBench {

	protected long doBatch(long numIterations) throws InterruptedException {
		SecureRandom rng = null;
		try {
//			rng = SecureRandom.getInstance("SHA1PRNG");
//			rng = SecureRandom.getInstance("NativePRNGNonBlocking");
//			rng = SecureRandom.getInstance("NativePRNGBlocking");
//			rng = SecureRandom.getInstance("PKCS11");
			rng = SecureRandom.getInstance("Windows-PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		long sum = 0L;
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}
}
