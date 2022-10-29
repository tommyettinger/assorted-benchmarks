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

import com.github.tommyettinger.digital.BitConversion;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Math.log() benchmarks.
 * <br>
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * Java 17 Hotspot:
 * <br>
 * LogApprox8FloatBench score: 77236216.000000 (77.24M 1816.2%)
 *                  uncertainty:   0.4%
 */
public final class LogApprox8FloatBench extends MicroBench {


    /**
	 * <a href="https://tech.ebayinc.com/engineering/fast-approximate-logarithms-part-iii-the-formulas/">From eBay Tech</a>.
	 * @param x
	 * @return
	 */
	public static float fastLog(float x)    // compute log(x) by reducing x to [0.75, 1.5)
	{
		/** MODIFY THIS SECTION **/
		// (x-1)*(a*(x-1) + b)/((x-1) + c) (line 8 of table 2)
		final float a = 0.338953f;
		final float b = 2.198599f;
		final float c = 1.523692f;
		//fexp + signif*(a*signif + b)/(signif + c)
		/** END SECTION  **/

		float signif, fexp;
		int exp;
		int ux1 = BitConversion.floatToIntBits(x);
		exp = (ux1 & 0x7F800000) >> 23;
		// actual exponent is exp-127, will subtract 127 later

		int greater = ux1 & 0x00400000;  // set if signif > 1.5
		signif = BitConversion.intBitsToFloat((ux1 & 0x007FFFFF) | 0x3f800000 - greater - greater);
		fexp = exp - 127 + (greater >>> 22);    // 126 instead of 127 compensates for division by 2
		--signif;
		return (fexp + signif * (a * signif + b) / (signif + c)) * 0.6931471805599453f;
	}
	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 0.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum += fastLog(sum * sum) * shrink;
		return numIterations;
	}

	public static void main(String[] args) {
		{
			System.out.println("log()");
			double absolute = 0.0, relative = 0.0, max = 0.0;
			float ctr = 0x1p-8f;
			for (int i = 0; i < 2048; i++) {
				final double error = Math.log(ctr) - fastLog(ctr);
				relative += error;
				max = Math.max(max, Math.abs(error));
				absolute += Math.abs(error);
				ctr += 0x1p-8f;
			}
			System.out.printf("Accuracy: absolute error %2.9f, relative error %2.9f, max error %2.9f",
					absolute * 0x1p-11, relative * 0x1p-11, max);
		}
	}
}
