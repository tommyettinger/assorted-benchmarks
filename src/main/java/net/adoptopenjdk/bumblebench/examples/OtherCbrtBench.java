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

import com.github.tommyettinger.ds.support.LaserRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/**
 * New laptop; Windows 10, 10th generation i7, Java 8
 * <br>
 * OtherCbrtBench score: 63464232.000000 (63.46M 1796.6%)
 *           uncertainty:   0.3%
 * <br>
 * New laptop; Windows 10, 10th generation i7, Java 15
 * <br>
 * OtherCbrtBench score: 62016896.000000 (62.02M 1794.3%)
 *           uncertainty:   0.3%
 */
public final class OtherCbrtBench extends MicroBench {
	public static float cbrt(float x) {
		int ix = Float.floatToRawIntBits(x);
		final int sign = ix & 0x80000000;
		ix &= 0x7FFFFFFF;
		final float x0 = x;
		ix = (ix>>>2) + (ix>>>4);
		ix += (ix>>>4);
		ix = ix + (ix>>>8) + 0x2a5137a0 | sign;
		x  = Float.intBitsToFloat(ix);
		x  = 0.33333334f*(2f*x + x0/(x*x));
		x  = 0.33333334f*(2f*x + x0/(x*x));
		return x;
	}
	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.1f;
		final float shrink = 1.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			sum -= cbrt(sum + i * shrink);
		return numIterations;
	}

	public static void main(String[] args) {
		LaserRandom random = new LaserRandom(12345, 6789);
		double sumError = 0.0, relativeError = 0.0;
		for (int i = 0; i < 1000000; i++) {
			float r = (random.nextFloat() - 0.5f) * random.next(10);
			float accurate = (float) Math.cbrt(r);
			float approx = cbrt(r);
			float error = accurate - approx;
			relativeError += error;
			sumError += Math.abs(error);
		}
		System.out.printf("Sum Error: %5.5f (averaged, %5.5f), Rel Error: %5.5f (averaged, %5.5f)", sumError, sumError / 0x100000, relativeError, relativeError / 0x100000);
	}
}
