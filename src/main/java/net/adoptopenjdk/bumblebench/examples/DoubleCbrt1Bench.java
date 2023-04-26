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
import com.github.tommyettinger.random.AceRandom;
import net.adoptopenjdk.bumblebench.core.MicroBench;

/*
 * Windows 10, 10th gen i7 mobile hexacore at 2.6 GHz:
 * <br>
 * HotSpot Java 8:
 * <br>
 *
 * <br>
 * OpenJ9 Java 15:
 * <br>
 *
 * <br>
 * HotSpot Java 16 (AdoptOpenJDK):
 * <br>
 *
 * <br>
 * HotSpot Java 17 (Adoptium):
 * <br>
 *
 * <br>
 * GraalVM Java 17:
 * <br>
 *
 * <br>
 * OpenJ9 Java 17 (Semeru):
 * <br>
 *
 * <br>
 * HotSpot Java 18 (Adoptium):
 * <br>
 *
 * <br>
 * HotSpot Java 19 (BellSoft):
 * <br>
 *
 */
public final class DoubleCbrt1Bench extends MicroBench {
	/**
	 * Double-precision cube root, tier 0.
	 * <br>
	 * <a href="https://stackoverflow.com/a/73354137">Credit to StackOverflow user wim</a>.
	 * @param x any double
	 * @return an approximation of the cube root for the given double
	 */
	public static double cbrt0(double x) {
		double a, y, r, r2_h, y_a2y4, ayy, diff;
		long ai, ai23, aim23;
		boolean small;

		a = Math.abs(x);
		small = a <  0.015625;                         // Scale large, small and/or subnormal numbers to avoid underflow, overflow or subnormal numbers
		a = small ? a * 0x1.0p+210 : a * 0.125;
		ai = BitConversion.doubleToLongBits(a);
		if (ai >= 0x7FF0000000000000L || x == 0.0){    // Inf, 0.0 and NaN
			r = x + x;
		}
		else
		{
			ai23 = 2 * (ai/3);                           // Integer division. The compiler, with suitable optimization level, should generate a much more efficient multiplication by 0xAAAAAAAAAAAAAAAB
			aim23 = 0x6A8EB53800000000L - ai23;          // This uses a similar idea as the "fast inverse square root" approximation, see https://en.wikipedia.org/wiki/Fast_inverse_square_root
			y = BitConversion.longBitsToDouble(aim23);   // y is an approximation of a^(-2/3)

			ayy = a * y * y;                          // First Newton iteration for f(y)=a^2-y^-3 to calculate a better approximation y=a^(-2/3)
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33333333333333333333 + y;

			ayy = a * y * y;                          // Second Newton iteration
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33523333333 + y;           // This is a small modification to the exact Newton parameter 1/3 which gives slightly better results

			ayy = a * y * y;                          // Third Newton iteration
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33333333333333333333 + y;

			r = y * a;                                // Now r = y * a is an approximation of a^(1/3), because y approximates a^(-2/3).

			r2_h = r * r;                             // Compute one pseudo Newton step with g(r)=a-r^3, but instead of dividing by f'(r)=3r^2 we multiply with
			// the approximation 0.3333...*y (division is usually a relatively expensive operation)
            double r2_l = r * r + -r2_h;              // For better accuracy we could split r*r=r^2 as r^2=r2_h+r2_l exactly, but don't now.
			diff = a - r2_h * r;
            diff = r2_l * -r + diff;                  // Compute diff=a-r^3 accurately: diff=(a-r*r2_h)-r*r2_l with two fma instructions
			diff *= 0.33333333333333333333;
			r = diff * y + r;                        // Now r approximates a^(1/3) well enough

            r2_h = r * r;                             // One final Halley iteration (omitted for now)
            r2_l = r * r + -r2_h;
            diff = r2_h * -r + a;
            diff = r2_l * -r + diff;
            double denom = a * 3.0 - 2.0 * diff;
            r = (diff/denom) * r + r;

			r = small ? r * 0x1.0p-70 : r * 2.0;   // Undo scaling
			r = Math.copySign(r, x);
		}
		return r;
	}
	/**
	 * Double-precision cube root, tier 1.
	 * <br>
	 * <a href="https://stackoverflow.com/a/73354137">Credit to StackOverflow user wim</a>.
	 * @param x any double
	 * @return an approximation of the cube root for the given double
	 */
	public static double cbrt1(double x) {
		double a, y, r, r2_h, y_a2y4, ayy, diff;
		long ai, ai23, aim23;
		boolean small;

		a = Math.abs(x);
		small = a <  0.015625;                         // Scale large, small and/or subnormal numbers to avoid underflow, overflow or subnormal numbers
		a = small ? a * 0x1.0p+210 : a * 0.125;
		ai = BitConversion.doubleToLongBits(a);
		if (ai >= 0x7FF0000000000000L || x == 0.0){    // Inf, 0.0 and NaN
			r = x + x;
		}
		else
		{
			ai23 = 2 * (ai/3);                           // Integer division. The compiler, with suitable optimization level, should generate a much more efficient multiplication by 0xAAAAAAAAAAAAAAAB
			aim23 = 0x6A8EB53800000000L - ai23;          // This uses a similar idea as the "fast inverse square root" approximation, see https://en.wikipedia.org/wiki/Fast_inverse_square_root
			y = BitConversion.longBitsToDouble(aim23);   // y is an approximation of a^(-2/3)

			ayy = a * y * y;                          // First Newton iteration for f(y)=a^2-y^-3 to calculate a better approximation y=a^(-2/3)
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33333333333333333333 + y;

			ayy = a * y * y;                          // Second Newton iteration
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33523333333 + y;           // This is a small modification to the exact Newton parameter 1/3 which gives slightly better results

			ayy = a * y * y;                          // Third Newton iteration
			y_a2y4 = y - ayy * ayy;
			y = y_a2y4 * 0.33333333333333333333 + y;

			r = y * a;                                // Now r = y * a is an approximation of a^(1/3), because y approximates a^(-2/3).

			r2_h = r * r;                             // Compute one pseudo Newton step with g(r)=a-r^3, but instead of dividing by f'(r)=3r^2 we multiply with
			// the approximation 0.3333...*y (division is usually a relatively expensive operation)
//            double r2_l = r * r + -r2_h;              // For better accuracy we could split r*r=r^2 as r^2=r2_h+r2_l exactly, but don't now.
			diff = a - r2_h * r;
//            diff = r2_l * -r + diff;                  // Compute diff=a-r^3 accurately: diff=(a-r*r2_h)-r*r2_l with two fma instructions
			diff *= 0.33333333333333333333;
			r = diff * y + r;                        // Now r approximates a^(1/3) well enough
/*
            r2_h = r * r;                             // One final Halley iteration (omitted for now)
            r2_l = r * r + -r2_h;
            diff = r2_h * -r + a;
            diff = r2_l * -r + diff;
            double denom = a * 3.0 - 2.0 * diff;
            r = (diff/denom) * r + r;
*/
			r = small ? r * 0x1.0p-70 : r * 2.0;   // Undo scaling
			r = Math.copySign(r, x);
		}
		return r;
	}
	/**
	 * Double-precision cube root, tier 2.
	 * <br>
	 * <a href="https://stackoverflow.com/a/73354137">Credit to StackOverflow user wim</a>.
	 * @param x any double
	 * @return an approximation of the cube root for the given double
	 */
	public static double cbrt2(double x) {
		double a, y, r, r2_h, y_a2y4, ayy, diff;
		long ai;

		a = Math.abs(x);
		ai = BitConversion.doubleToLongBits(a);
		ai = 2 * (ai / 3);                           // Integer division. The compiler, with suitable optimization level, should generate a much more efficient multiplication by 0xAAAAAAAAAAAAAAAB
		ai = 0x6A8EB53800000000L - ai;          // This uses a similar idea as the "fast inverse square root" approximation, see https://en.wikipedia.org/wiki/Fast_inverse_square_root
		y = BitConversion.longBitsToDouble(ai);   // y is an approximation of a^(-2/3)

		ayy = a * y * y;                          // First Newton iteration for f(y)=a^2-y^-3 to calculate a better approximation y=a^(-2/3)
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;

		ayy = a * y * y;                          // Second Newton iteration
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;     // 0.33523333333 is a small modification to the exact Newton parameter 1/3 which might give slightly better results (it doesn't here).

		ayy = a * y * y;                          // Third Newton iteration
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;

		r = y * a;                                // Now r = y * a is an approximation of a^(1/3), because y approximates a^(-2/3).

		r2_h = r * r;                             // Compute one pseudo Newton step with g(r)=a-r^3, but instead of dividing by f'(r)=3r^2 we multiply with
		// the approximation 0.3333...*y (division is usually a relatively expensive operation)
//            double r2_l = r * r + -r2_h;              // For better accuracy we could split r*r=r^2 as r^2=r2_h+r2_l exactly, but don't now.
		diff = a - r2_h * r;
//            diff = r2_l * -r + diff;                  // Compute diff=a-r^3 accurately: diff=(a-r*r2_h)-r*r2_l with two fma instructions
		diff *= 0.33333333333333333333;
		r = diff * y + r;                        // Now r approximates a^(1/3) well enough
/*
            r2_h = r * r;                             // One final Halley iteration (omitted for now)
            r2_l = r * r + -r2_h;
            diff = r2_h * -r + a;
            diff = r2_l * -r + diff;
            double denom = a * 3.0 - 2.0 * diff;
            r = (diff/denom) * r + r;
*/
		r = Math.copySign(r, x);
		return r;
	}
	/**
	 * Double-precision cube root, tier 3.
	 * <br>
	 * <a href="https://stackoverflow.com/a/73354137">Credit to StackOverflow user wim</a>.
	 * @param x any double
	 * @return an approximation of the cube root for the given double
	 */
	public static double cbrt3(double x) {
		double a, y, r, r2_h, y_a2y4, ayy, diff;
		long ai, ai23, aim23;

		a = Math.abs(x);
		ai = BitConversion.doubleToLongBits(a);
		ai23 = 2 * (ai / 3);                           // Integer division. The compiler, with suitable optimization level, should generate a much more efficient multiplication by 0xAAAAAAAAAAAAAAAB
		aim23 = 0x6A8EB53800000000L - ai23;          // This uses a similar idea as the "fast inverse square root" approximation, see https://en.wikipedia.org/wiki/Fast_inverse_square_root
		y = BitConversion.longBitsToDouble(aim23);   // y is an approximation of a^(-2/3)

		ayy = a * y * y;                          // First Newton iteration for f(y)=a^2-y^-3 to calculate a better approximation y=a^(-2/3)
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;

		ayy = a * y * y;                          // Second Newton iteration
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;     // 0.33523333333 is a small modification to the exact Newton parameter 1/3 which might give slightly better results (it doesn't here).

		ayy = a * y * y;                          // Third Newton iteration
		y_a2y4 = y - ayy * ayy;
		y += y_a2y4 * 0.33333333333333333333;

		r = y * a;                                // Now r = y * a is an approximation of a^(1/3), because y approximates a^(-2/3).

		r2_h = r * r;                             // Compute one pseudo Newton step with g(r)=a-r^3, but instead of dividing by f'(r)=3r^2 we multiply with
		// the approximation 0.3333...*y (division is usually a relatively expensive operation)
//            double r2_l = r * r + -r2_h;              // For better accuracy we could split r*r=r^2 as r^2=r2_h+r2_l exactly, but don't now.
		diff = a - r2_h * r;
//            diff = r2_l * -r + diff;                  // Compute diff=a-r^3 accurately: diff=(a-r*r2_h)-r*r2_l with two fma instructions
		diff *= 0.33333333333333333333;
		r = diff * y + r;                        // Now r approximates a^(1/3) well enough
/*
            r2_h = r * r;                             // One final Halley iteration (omitted for now)
//            r2_l = r * r - r2_h;
            diff = a - r2_h * r;
//            diff = diff - r2_l * r;
            double denom = a * 3.0 - 2.0 * diff;
            r = (diff/denom) * r + r;
*/
		r = Math.copySign(r, x);
		return r;
	}

	protected long doBatch (long numIterations) throws InterruptedException {
		double sum = 0.01, inc = -0.5;
		final double shrink = 1.6180339887498949 / numIterations;
		for (long i = 0; i < numIterations; i++)
			  sum = cbrt1(sum + sum + (inc += shrink));
		return numIterations;
	}

	public static void main(String[] args) {
		AceRandom random = new AceRandom(0x123456789ABCDEF0L);
		double sumError0 = 0.0, relativeError0 = 0.0, maxRelError0 = 0.0;
		double sumError1 = 0.0, relativeError1 = 0.0, maxRelError1 = 0.0;
		double sumError2 = 0.0, relativeError2 = 0.0, maxRelError2 = 0.0;
		double sumError3 = 0.0, relativeError3 = 0.0, maxRelError3 = 0.0;
		double re;
		int i = 0;
		for (; i < 1000000; i++) {
			double r = random.nextExclusiveSignedDouble() * (random.next(10)+1);
			double accurate = Math.cbrt(r);
			if(accurate == 0.0)
				continue;
			double approx0 = cbrt0(r);
			double error0 = accurate - approx0;
			re = Math.abs(error0 / accurate);
			relativeError0 += re;
			maxRelError0 = Math.max(maxRelError0, re);
			sumError0 += Math.abs(error0);
			double approx1 = cbrt1(r);
			double error1 = accurate - approx1;
			re = Math.abs(error1 / accurate);
			relativeError1 += re;
			maxRelError1 = Math.max(maxRelError1, re);
			sumError1 += Math.abs(error1);
			double approx2 = cbrt2(r);
			double error2 = accurate - approx2;
			re = Math.abs(error2 / accurate);
			relativeError2 += re;
			maxRelError2 = Math.max(maxRelError2, re);
			sumError2 += Math.abs(error2);
			double approx3 = cbrt3(r);
			double error3 = accurate - approx3;
			re = Math.abs(error3 / accurate);
			relativeError3 += re;
			maxRelError3 = Math.max(maxRelError3, re);
			sumError3 += Math.abs(error3);
		}
		System.out.println("Tier 0:");
		System.out.printf("Sum Error: %1.25f (averaged, %1.25f), Rel Error: %1.25f (averaged, %1.25f, max %1.25f)\n", sumError0, sumError0 / i, relativeError0, relativeError0 / i, maxRelError0);
		System.out.println("Tier 1:");
		System.out.printf("Sum Error: %1.25f (averaged, %1.25f), Rel Error: %1.25f (averaged, %1.25f, max %1.25f)\n", sumError1, sumError1 / i, relativeError1, relativeError1 / i, maxRelError1);
		System.out.println("Tier 2:");
		System.out.printf("Sum Error: %1.25f (averaged, %1.25f), Rel Error: %1.25f (averaged, %1.25f, max %1.25f)\n", sumError2, sumError2 / i, relativeError2, relativeError2 / i, maxRelError2);
		System.out.println("Tier 3:");
		System.out.printf("Sum Error: %1.25f (averaged, %1.25f), Rel Error: %1.25f (averaged, %1.25f, max %1.25f)\n", sumError3, sumError3 / i, relativeError3, relativeError3 / i, maxRelError3);
	}
}
