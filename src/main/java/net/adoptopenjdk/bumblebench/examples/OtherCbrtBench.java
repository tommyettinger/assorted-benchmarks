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
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 * OtherCbrtBench score: 63768328.000000 (63.77M 1797.1%)
 *           uncertainty:   0.6%
 * <br>
 * OpenJ9 Java 15:
 * <br>
 * OtherCbrtBench score: 61440084.000000 (61.44M 1793.4%)
 *           uncertainty:   0.7%
 * <br>
 * HotSpot Java 16:
 * <br>
 * OtherCbrtBench score: 64276212.000000 (64.28M 1797.9%)
 *           uncertainty:   0.7%
 */
public final class OtherCbrtBench extends MicroBench {
	public static float cbrt(float x) {
		final float x0 = x;
		int ix = Float.floatToRawIntBits(x0);
		final int sign = ix & 0x80000000;
		ix &= 0x7FFFFFFF;
		ix = (ix>>>2) + (ix>>>4);
		ix += (ix>>>4);
		ix += (ix>>>8) + 0x2a5137a0;
		x  = Float.intBitsToFloat(ix|sign);
		x  = 0.33333334f*(2f*x + x0/(x*x));
		x  = 0.33333334f*(2f*x + x0/(x*x));
		return x;
	}

	/**
	 * A cube root method by Moroz et al; not recommended.
	 * I checked this, but it a) needed changes to handle negative numbers without returning
	 * NaN, and b) has much higher absolute and relative error than the above cbrt().
	 * Specifically, on one million random floats between -512 (inclusive) and 512 (exclusive),
	 * the above method gets 0.95888 absolute error and 0.00074 relative error, while this
	 * Householder method gets 76.51524 absolute error and 0.01686 relative error.
	 * From https://res.mdpi.com/d_attachment/energies/energies-14-01058/article_deploy/energies-14-01058-v2.pdf
	 * @param x a float to get the cube root for
	 * @return the cube root, approximately
	 */
	public static float cbrtHouseholder(float x) {
		final float k1 = 1.752319676f;
		final float k2 = 1.2509524245f;
		final float k3 = 0.5093818292f;
		int ix = Float.floatToRawIntBits(x);
		final int sign = ix&0x80000000;
		ix &= 0x7FFFFFFF;
		// this next line is different from what the paper used, but
		// "i * 0xAAAAAAABL >>> 33" is identical to "i / 3" for positive i.
		float y = Float.intBitsToFloat(0x548c2b4b-(int)(ix * 0xAAAAAAABL >>> 33)^sign);
		final float c = x*y*y*y;
		y*=(k1-c*(k2-k3*c));
		final float d = x*y*y;
		return d*(1.0f+0.333333333333f*(1.0f-d*y));
	}
	public static float cbrtProblem(float x) {
		final float k1 = 1.752319676f;
		final float k2 = 1.2509524245f;
		final float k3 = 0.5093818292f;
		float y = Float.intBitsToFloat(0x548c2b4b-(int)(Float.floatToRawIntBits(x) * 0x55555556L >>> 32));
		final float c = x*y*y*y;
		y*=(k1-c*(k2-k3*c));
		final float d = x*y*y;
		return d*(1.0f+0.333333333333f*(1.0f-d*y));
	}
	protected long doBatch (long numIterations) throws InterruptedException {
		float sum = 0.01f, inc = -0.5f;
		final float shrink = 1.6180339887498949f / numIterations;
		for (long i = 0; i < numIterations; i++)
			  sum = cbrt(sum + sum + (inc += shrink));
		return numIterations;
	}

	public static void main(String[] args) {
		LaserRandom random = new LaserRandom(12345, 6789);
		double sumError = 0.0, relativeError = 0.0;
		double sumErrorH = 0.0, relativeErrorH = 0.0;
		double sumErrorP = 0.0, relativeErrorP = 0.0;
		int i = 0;
		for (; i < 1000000; i++) {
			float r = (random.nextFloat() - 0.5f) * random.next(10);
//			float r = (random.nextFloat()) * random.next(9);
			float accurate = (float) Math.cbrt(r);
			float approx = cbrt(r);
			float approxH = cbrtHouseholder(r);
			float approxP = cbrtProblem(r);
			if(!Float.isFinite(approxP))
			{
				System.out.println(r + " was a problem! On iteration " + i);
				approxP = -r;
			}
			float error = accurate - approx;
			relativeError += error;
			sumError += Math.abs(error);
			float errorH = accurate - approxH;
			relativeErrorH += errorH;
			sumErrorH += Math.abs(errorH);
			float errorP = accurate - approxP;
			relativeErrorP += errorP;
			sumErrorP += Math.abs(errorP);
		}
		System.out.println("Newton-Raphson:");
		System.out.printf("Sum Error: %5.5f (averaged, %5.5f), Rel Error: %5.5f (averaged, %5.5f)", sumError, sumError / i, relativeError, relativeError / i);
		System.out.println("\nHouseholder:");
		System.out.printf("Sum Error: %5.5f (averaged, %5.5f), Rel Error: %5.5f (averaged, %5.5f)", sumErrorH, sumErrorH / i, relativeErrorH, relativeErrorH / i);
		System.out.println("\nProblem Householder:");
		System.out.printf("Sum Error: %5.5f (averaged, %5.5f), Rel Error: %5.5f (averaged, %5.5f)", sumErrorP, sumErrorP / i, relativeErrorP, relativeErrorP / i);
	}
}
