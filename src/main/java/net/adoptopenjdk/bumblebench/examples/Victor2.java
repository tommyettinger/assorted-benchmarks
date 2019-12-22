/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package net.adoptopenjdk.bumblebench.examples;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

import java.io.Serializable;

/** Encapsulates a 2D vector. Allows chaining methods by returning a reference to itself
 * @author badlogicgames@gmail.com */
public class Victor2 implements Serializable, Vector<Victor2> {
	private static final long serialVersionUID = 913902788239530931L;

	public final static Victor2 X = new Victor2(1, 0);
	public final static Victor2 Y = new Victor2(0, 1);
	public final static Victor2 Zero = new Victor2(0, 0);

	/** the x-component of this vector **/
	public float x;
	/** the y-component of this vector **/
	public float y;

	/** Constructs a new vector at (0,0) */
	public Victor2() {
	}

	/** Constructs a vector with the given components
	 * @param x The x-component
	 * @param y The y-component */
	public Victor2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	/** Constructs a vector from the given vector
	 * @param v The vector */
	public Victor2(Victor2 v) {
		set(v);
	}

	@Override
	public Victor2 cpy () {
		return new Victor2(this);
	}

	public static float len (float x, float y) {
		return (float)Math.sqrt(x * x + y * y);
	}

	@Override
	public float len () {
		return (float)Math.sqrt(x * x + y * y);
	}

	public static float len2 (float x, float y) {
		return x * x + y * y;
	}

	@Override
	public float len2 () {
		return x * x + y * y;
	}

	@Override
	public Victor2 set (Victor2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	/** Sets the components of this vector
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining */
	public Victor2 set (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	@Override
	public Victor2 sub (Victor2 v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	/** Substracts the other vector from this vector.
	 * @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return This vector for chaining */
	public Victor2 sub (float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	@Override
	public Victor2 nor () {
		float len = len();
		if (len != 0) {
			x /= len;
			y /= len;
		}
		return this;
	}

	@Override
	public Victor2 add (Victor2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	/** Adds the given components to this vector
	 * @param x The x-component
	 * @param y The y-component
	 * @return This vector for chaining */
	public Victor2 add (float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public static float dot (float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	@Override
	public float dot (Victor2 v) {
		return x * v.x + y * v.y;
	}

	public float dot (float ox, float oy) {
		return x * ox + y * oy;
	}

	@Override
	public Victor2 scl (float scalar) {
		x *= scalar;
		y *= scalar;
		return this;
	}

	/** Multiplies this vector by a scalar
	 * @return This vector for chaining */
	public Victor2 scl (float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}

	@Override
	public Victor2 scl (Victor2 v) {
		this.x *= v.x;
		this.y *= v.y;
		return this;
	}

	@Override
	public Victor2 mulAdd (Victor2 vec, float scalar) {
		this.x += vec.x * scalar;
		this.y += vec.y * scalar;
		return this;
	}

	@Override
	public Victor2 mulAdd (Victor2 vec, Victor2 mulVec) {
		this.x += vec.x * mulVec.x;
		this.y += vec.y * mulVec.y;
		return this;
	}

	public static float dst (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	@Override
	public float dst (Victor2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the distance between this and the other vector */
	public float dst (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public static float dst2 (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public float dst2 (Victor2 v) {
		final float x_d = v.x - x;
		final float y_d = v.y - y;
		return x_d * x_d + y_d * y_d;
	}

	/** @param x The x-component of the other vector
	 * @param y The y-component of the other vector
	 * @return the squared distance between this and the other vector */
	public float dst2 (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}

	@Override
	public Victor2 limit (float limit) {
		return limit2(limit * limit);
	}

	@Override
	public Victor2 limit2 (float limit2) {
		float len2 = len2();
		if (len2 > limit2) {
			return scl((float)Math.sqrt(limit2 / len2));
		}
		return this;
	}

	@Override
	public Victor2 clamp (float min, float max) {
		final float len2 = len2();
		if (len2 == 0f) return this;
		float max2 = max * max;
		if (len2 > max2) return scl((float)Math.sqrt(max2 / len2));
		float min2 = min * min;
		if (len2 < min2) return scl((float)Math.sqrt(min2 / len2));
		return this;
	}

	@Override
	public Victor2 setLength (float len) {
		return setLength2(len * len);
	}

	@Override
	public Victor2 setLength2 (float len2) {
		float oldLen2 = len2();
		return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float)Math.sqrt(len2 / oldLen2));
	}

	/** Converts this {@code Victor2} to a string in the format {@code (x,y)}.
	 * @return a string representation of this object. */
	@Override
	public String toString () {
		return "(" + x + "," + y + ")";
	}

	/** Sets this {@code Victor2} to the value represented by the specified string according to the format of {@link #toString()}.
	 * @param v the string.
	 * @return this vector for chaining */
	public Victor2 fromString (String v) {
		int s = v.indexOf(',', 1);
		if (s != -1 && v.charAt(0) == '(' && v.charAt(v.length() - 1) == ')') {
			try {
				float x = Float.parseFloat(v.substring(1, s));
				float y = Float.parseFloat(v.substring(s + 1, v.length() - 1));
				return this.set(x, y);
			} catch (NumberFormatException ex) {
				// Throw a GdxRuntimeException
			}
		}
		throw new GdxRuntimeException("Malformed Victor2: " + v);
	}

	/** Left-multiplies this vector by the given matrix
	 * @param mat the matrix
	 * @return this vector */
	public Victor2 mul (Matrix3 mat) {
		float x = this.x * mat.val[0] + this.y * mat.val[3] + mat.val[6];
		float y = this.x * mat.val[1] + this.y * mat.val[4] + mat.val[7];
		this.x = x;
		this.y = y;
		return this;
	}

	/** Calculates the 2D cross product between this and the given vector.
	 * @param v the other vector
	 * @return the cross product */
	public float crs (Victor2 v) {
		return this.x * v.y - this.y * v.x;
	}

	/** Calculates the 2D cross product between this and the given vector.
	 * @param x the x-coordinate of the other vector
	 * @param y the y-coordinate of the other vector
	 * @return the cross product */
	public float crs (float x, float y) {
		return this.x * y - this.y * x;
	}

	/** @return the angle in degrees of this vector (point) relative to the x-axis. Angles are towards the positive y-axis (typically
	 *         counter-clockwise) and between 0 and 360. */
	public float angle () {
		float angle = (float)Math.atan2(y, x) * MathUtils.radiansToDegrees;
		if (angle < 0) angle += 360;
		return angle;
	}

	/** @return the angle in degrees of this vector (point) relative to the given vector. Angles are towards the positive y-axis
	 *         (typically counter-clockwise.) between -180 and +180 */
	public float angle (Victor2 reference) {
		return (float)Math.atan2(crs(reference), dot(reference)) * MathUtils.radiansToDegrees;
	}

	/** @return the angle in radians of this vector (point) relative to the x-axis. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise) */
	public float angleRad () {
		return (float)Math.atan2(y, x);
	}

	/** @return the angle in radians of this vector (point) relative to the given vector. Angles are towards the positive y-axis.
	 *         (typically counter-clockwise.) */
	public float angleRad (Victor2 reference) {
		return (float)Math.atan2(crs(reference), dot(reference));
	}

	/** Sets the angle of the vector in degrees relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param degrees The angle in degrees to set. */
	public Victor2 setAngle (float degrees) {
		return setAngleRad(degrees * MathUtils.degreesToRadians);
	}

	/** Sets the angle of the vector in radians relative to the x-axis, towards the positive y-axis (typically counter-clockwise).
	 * @param radians The angle in radians to set. */
	public Victor2 setAngleRad (float radians) {
		this.set(len(), 0f);
		this.rotateRad(radians);

		return this;
	}

	/** Rotates the Victor2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param degrees the angle in degrees */
	public Victor2 rotate (float degrees) {
		return rotateRad(degrees * MathUtils.degreesToRadians);
	}

	/** Rotates the Victor2 by the given angle, counter-clockwise assuming the y-axis points up.
	 * @param radians the angle in radians */
	public Victor2 rotateRad (float radians) {
		float cos = (float)Math.cos(radians);
		float sin = (float)Math.sin(radians);

		float newX = this.x * cos - this.y * sin;
		float newY = this.x * sin + this.y * cos;

		this.x = newX;
		this.y = newY;

		return this;
	}

	/** Rotates the Victor2 by 90 degrees in the specified direction, where >= 0 is counter-clockwise and < 0 is clockwise. */
	public Victor2 rotate90 (int dir) {
		float x = this.x;
		if (dir >= 0) {
			this.x = -y;
			y = x;
		} else {
			this.x = y;
			y = -x;
		}
		return this;
	}

	@Override
	public Victor2 lerp (Victor2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public Victor2 interpolate (Victor2 target, float alpha, Interpolation interpolation) {
		return lerp(target, interpolation.apply(alpha));
	}

	@Override
	public Victor2 setToRandomDirection () {
		float theta = MathUtils.random(0f, MathUtils.PI2);
		return this.set(MathUtils.cos(theta), MathUtils.sin(theta));
	}

	@Override
	public int hashCode () {
		
		////Two different XLCG random number generator steps, added, keeping only the upper 32 bits.
		////This may be a little finicky regarding input, but it doesn't give anomalous results on GWT.
		////Magic numbers used are all within a few bits of (2 to the 64) divided by a harmonious number or its square.
		////See http://extremelearning.com.au/unreasonable-effectiveness-of-quasirandom-sequences/ for an intro.
		////The only requirement for an XLCG is that the XOR constant, modulo 8, equals 5, and the multiplier constant,
		////modulo 8, equals 3; this is easy with hex because the XOR constant ends in D or 5, the other in B or 3.
		////Both this and the Rosenberg-Strong pairing function below can handle 25 million Victor2 in an ObjectMap.
//		return (int)(((NumberUtils.floatToIntBits(x) ^ 0xC13FA9A902A6328DL) * 0xD1B54A32D192ED0BL
//				+ (NumberUtils.floatToIntBits(y) ^ 0x91E10DA5C79E7B1DL) * 0xABC98388FB8FAC03L) >>> 32);
		////works almost as well as commented code above with XLCG, slightly faster
		return (int)(((NumberUtils.floatToIntBits(x) * 0xC13FA9A902A6328FL)
				+ (NumberUtils.floatToIntBits(y) * 0x91E10DA5C79E7B1DL)) >>> 32);

//		final int h = NumberUtils.floatToIntBits(x) * 0xC13FA9A9 + NumberUtils.floatToIntBits(y) * 0x91E10DA5;
//		return h ^ h >>> 16 ^ h >>> 21;
//		final int h = NumberUtils.floatToIntBits(x * 0.7548776662466927f + 0.6710436067037893f) + NumberUtils.floatToIntBits(y * 0.5698402909980532f + 0.8191725133961645f);
//		return h ^ h >>> 16 ^ h >>> 21;
//		int xx = NumberUtils.floatToIntBits(x), yy = NumberUtils.floatToIntBits(y);
////		////There was lots of fiddling with this; it seems very strong now. Yes, one of those is a signed shift.
//		xx ^= xx >> 16 ^ xx >>> 21;
//		yy ^= yy >> 16 ^ yy >>> 21;
////		////Rosenberg-Strong Pairing Function
////		////assigns numbers to (x,y) pairs, assigning bigger numbers to bigger shells; the shell is max(x,y).
//		xx += (xx > yy ? xx * xx + xx - yy : yy * yy);
////		////Gray Code, makes any sequential values for xx vary by exactly one bit
////		////only used here to scramble visual patterns slightly and to end with a bitwise operation for GWT reasons.
////		////There probably are many Victor2 values where this hashCode() will be different on GWT; the above XLCG way
////		////shouldn't have the same issues, though it will be slower on GWT.
//		return xx ^ xx >>> 1;

		////Cantor Pairing Function; not quite as fast?
//		return yy + ((xx+yy) * (xx+yy+1) >> 1);

//		return 0xC13F * xx ^ 0x91E1 * yy;
		//return 0xC13F * (xx ^ xx >>> 16) + 0x91E1 * (yy ^ yy >>> 16);
		
//		final long r = (NumberUtils.floatToIntBits(x) ^ 0xa0761d65L) * (NumberUtils.floatToIntBits(y) ^ 0x8ebc6af1L);
//		return ((int)(r - (r >> 32)));
		
		//r = ((r - (r >> 32)) + 0xEB44ACCBL) * 0xE7037ED1L;
//		return ((int)(r - (r >> 32)));

	}
//		final int result = 0xC13F * NumberUtils.floatToIntBits(x) + 0x91E1 * NumberUtils.floatToIntBits(y);
	// ^ xx + yy >>> 24;

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Victor2 other = (Victor2)obj;
		if (NumberUtils.floatToIntBits(x) != NumberUtils.floatToIntBits(other.x)) return false;
		if (NumberUtils.floatToIntBits(y) != NumberUtils.floatToIntBits(other.y)) return false;
		return true;
	}

	@Override
	public boolean epsilonEquals (Victor2 other, float epsilon) {
		if (other == null) return false;
		if (Math.abs(other.x - x) > epsilon) return false;
		if (Math.abs(other.y - y) > epsilon) return false;
		return true;
	}

	/** Compares this vector with the other vector, using the supplied epsilon for fuzzy equality testing.
	 * @return whether the vectors are the same. */
	public boolean epsilonEquals (float x, float y, float epsilon) {
		if (Math.abs(x - this.x) > epsilon) return false;
		if (Math.abs(y - this.y) > epsilon) return false;
		return true;
	}

	/**
	 * Compares this vector with the other vector using MathUtils.FLOAT_ROUNDING_ERROR for fuzzy equality testing
	 *
	 * @param other other vector to compare
	 * @return true if vector are equal, otherwise false
	 */
	public boolean epsilonEquals (final Victor2 other) {
		return epsilonEquals(other, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	/**
	 * Compares this vector with the other vector using MathUtils.FLOAT_ROUNDING_ERROR for fuzzy equality testing
	 *
	 * @param x x component of the other vector to compare
	 * @param y y component of the other vector to compare
	 * @return true if vector are equal, otherwise false
	 */
	public boolean epsilonEquals (float x, float y) {
		return epsilonEquals(x, y, MathUtils.FLOAT_ROUNDING_ERROR);
	}

	@Override
	public boolean isUnit () {
		return isUnit(0.000000001f);
	}

	@Override
	public boolean isUnit (final float margin) {
		return Math.abs(len2() - 1f) < margin;
	}

	@Override
	public boolean isZero () {
		return x == 0 && y == 0;
	}

	@Override
	public boolean isZero (final float margin) {
		return len2() < margin;
	}

	@Override
	public boolean isOnLine (Victor2 other) {
		return MathUtils.isZero(x * other.y - y * other.x);
	}

	@Override
	public boolean isOnLine (Victor2 other, float epsilon) {
		return MathUtils.isZero(x * other.y - y * other.x, epsilon);
	}

	@Override
	public boolean isCollinear (Victor2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinear (Victor2 other) {
		return isOnLine(other) && dot(other) > 0f;
	}

	@Override
	public boolean isCollinearOpposite (Victor2 other, float epsilon) {
		return isOnLine(other, epsilon) && dot(other) < 0f;
	}

	@Override
	public boolean isCollinearOpposite (Victor2 other) {
		return isOnLine(other) && dot(other) < 0f;
	}

	@Override
	public boolean isPerpendicular (Victor2 vector) {
		return MathUtils.isZero(dot(vector));
	}

	@Override
	public boolean isPerpendicular (Victor2 vector, float epsilon) {
		return MathUtils.isZero(dot(vector), epsilon);
	}

	@Override
	public boolean hasSameDirection (Victor2 vector) {
		return dot(vector) > 0;
	}

	@Override
	public boolean hasOppositeDirection (Victor2 vector) {
		return dot(vector) < 0;
	}

	@Override
	public Victor2 setZero () {
		this.x = 0;
		this.y = 0;
		return this;
	}
}
