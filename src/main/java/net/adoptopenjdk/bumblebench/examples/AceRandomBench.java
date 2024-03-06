
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
 * AceRandomBench score: 695007488.000000 (695.0M 2035.9%)
 *            uncertainty:   3.2%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 * AceRandomBench score: 1046176256.000000 (1.046G 2076.8%)
 *            uncertainty:   3.1%
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 * AceRandomBench score: 1596342272.000000 (1.596G 2119.1%)
 *            uncertainty:   0.4%
 * <br>
 * GraalVM Java 17:
 * <br>
 * AceRandomBench score: 1870299520.000000 (1.870G 2134.9%)
 *            uncertainty:   0.7%
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 * AceRandomBench score: 1585510784.000000 (1.586G 2118.4%)
 *            uncertainty:   1.4%
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 * AceRandomBench score: 1752449536.000000 (1.752G 2128.4%)
 *            uncertainty:   1.6%
 * <br>
 * GraalVM 21 (Oracle)
 * <br>
 * On the first try, this is clearly incorrect, it looks like the entire loop was elided.
 * <br>
 * AceRandomBench score: 652692438696862300000000000.000000 (652.7E+24 6174.3%)
 *            uncertainty:  24.0%
 * <br>
 * On the next try, we keep `sum` in a private variable and write it at the end of the benchmark, to ensure the timing
 * actually counts the RNG calls (since now they have a lasting effect).
 * <br>
 * AceRandomBench score: 567918336.000000 (567.9M 2015.7%)
 *            uncertainty:   3.4%
 * <br>
 * Now we're using PGO, and the results are drastically better. The steps used here:
 * <pre>
 * echo Building an instrumented JAR:
 * native-image.cmd --pgo-instrument -jar target/BumbleBench.jar
 * echo Running the instrumented JAR to collect profile information:
 * BumbleBench.exe AceRandomBench
 * echo Building a PGO JAR:
 * native-image.cmd --pgo=default.iprof -jar target/BumbleBench.jar
 * echo Running the PGO JAR:
 * BumbleBench.exe AceRandomBench
 * </pre>
 * <br>
 * AceRandomBench score: 1705748992.000000 (1.706G 2125.7%)
 *            uncertainty:   3.1%
 */
public final class AceRandomBench extends MicroBench {

	private long sum = 0L;
	protected long doBatch(long numIterations) throws InterruptedException {
		AceRandom rng = new AceRandom(0x12345678);
		for (long i = 0; i < numIterations; i++)
			sum += rng.nextLong();
		return numIterations;
	}

	/**
	 * Optionally implemented by subclasses and called at the end of a run to verify
	 * whether the run was correct or not. Defaults to true. If false, an ERROR message
	 * is printed instead of the final score. Implementing methods may output their own
	 * error message(s) as well.
	 */
	@Override
	protected boolean verify() {
		System.out.println("Final sum was: ");
		System.out.println(sum);
		System.out.println();
		return super.verify();
	}
}
