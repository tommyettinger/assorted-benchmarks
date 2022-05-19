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

import com.github.tommyettinger.random.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * OtherCbrtPositiveBench score: 64066800.000000 (64.07M 1797.5%)
 *                   uncertainty:   0.5%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * OtherCbrtPositiveBench score: 63097600.000000 (63.10M 1796.0%)
 *                   uncertainty:   1.0%
 * <br>
 * HotSpot Java 16:
 * <br>
 * OtherCbrtPositiveBench score: 64489532.000000 (64.49M 1798.2%)
 *                   uncertainty:   0.8%
 */
public final class OtherCbrtPositiveBench extends MicroBench {
	private static final float ONE_THIRD = 1f / 3f;
	private static float cbrtPositive(float x) {
		int ix = Float.floatToIntBits(x);
		final float x0 = x;
		ix = (ix>>>2) + (ix>>>4);
		ix += (ix>>>4);
		ix += (ix>>>8) + 0x2a5137a0;
		x  = Float.intBitsToFloat(ix);
		x  = ONE_THIRD*(2f*x + x0/(x*x));
		x  = ONE_THIRD*(2f*x + x0/(x*x));
		return x;
	}
	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.01f, inc = 0.01f;
		final float shrink = 1.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			  sum = cbrtPositive(sum + sum + (inc += shrink));
		return numIterations;
	}

	public static void main(String[] args) {
		LaserRandom random = new LaserRandom(12345, 6789);
		double sumError = 0.0, relativeError = 0.0;
		for (int i = 0; i < 0x100000; i++) {
			float r = (random.nextFloat()) * random.next(10);
			float accurate = (float) Math.cbrt(r);
			float approx = cbrtPositive(r);
			float error = accurate - approx;
			relativeError += error;
			sumError += Math.abs(error);
		}
		System.out.printf("Sum Error: %5.5f (averaged, %5.5f), Rel Error: %5.5f (averaged, %5.5f)", sumError, sumError / 0x100000, relativeError, relativeError / 0x100000);
	}
}
