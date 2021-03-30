/* ******************************************************************************
 * Copyright 2020 See AUTHORS file.
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
package com.github.tommyettinger.squidlib;

import static com.badlogic.gdx.math.MathUtils.PI;

/**
 * Math helper functions.
 */
public final class NumberTools2 {
    private NumberTools2() {
    }
    
    static public float atan2_imuli0(float y, float x){

        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;                                                  // [0,1]
        float th = (0.97239411f - 0.19194795f * z * z) * z;                                // [0,π/4]
        if(invert) th = 1.5707963267948966f - th;                                           // [0,π/2]
        if(x < 0) th = 3.141592653589793f - th;                                            // [0,π]
        return Math.copySign(th, y);                                                       // [-π,π]
    }
    
    public static float atan2_imuli_(float y, float x){
        if (y == 0f && x >= 0f) return 0f;
        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;
        z = (((((0.022520265292560102f) * z) - (0.054640279287594046f)) * z - (0.0025821297967229097f)) * z + (0.1597659389184251f)) * z - (0.000025146481008519463f);
        if(invert) z = 0.25f - z;
        if(x < 0) z = 0.5f - z;
        return y < 0 ? 1f - z : z;
    }

    /**
     * Credit to imuli and Nic Taylor; imuli commented on
     * <a href="https://www.dsprelated.com/showarticle/1052.php">Taylor's article</a> with very useful info.
     * @param y
     * @param x
     * @return
     */
    // credit to https://www.dsprelated.com/showarticle/1052.php
    public static float atan2_quartic(float y, float x)
    {
        if (y == 0f && x >= 0f) return 0f;
        float ay = Math.abs(y), ax = Math.abs(x);
        boolean invert = ay > ax;
        float z = invert ? ax/ay : ay/ax;
        z = ((((0.141499f * z) - 0.343315f) * z - 0.016224f) * z + 1.003839f) * z - 0.000158f;
        if(invert) z = 1.5707963267948966f - z;
        if(x < 0) z = 3.141592653589793f - z;
        return Math.copySign(z, y);
    }

    static public float atan2_nt(float y, float x)
    {
        if (x != 0.0f)
        {
            if (Math.abs(x) > Math.abs(y))
            { 
                final float z = y / x;
                if (x > 0.0)
                {
                    // atan2(y,x) = atan(y/x) if x > 0
                    return (0.97239411f - 0.19194795f * z * z) * z;
                }
                else if (y >= 0.0)
                {
                    // atan2(y,x) = atan(y/x) + PI if x < 0, y >= 0
                    return (0.97239411f - 0.19194795f * z * z) * z + PI;
                }
                else
                {
                    // atan2(y,x) = atan(y/x) - PI if x < 0, y < 0
                    return (0.97239411f - 0.19194795f * z * z) * z - PI;
                }
            }
            else // Use property atan(y/x) = PI/2 - atan(x/y) if |y/x| > 1.
            {
                final float z = x / y;
                if (y > 0.0)
                {
                    // atan2(y,x) = PI/2 - atan(x/y) if |y/x| > 1, y > 0
                    return 1.5707963267948966f - (0.97239411f - 0.19194795f * z * z) * z;
                }
                else
                {
                    // atan2(y,x) = -PI/2 - atan(x/y) if |y/x| > 1, y < 0
                    return -1.5707964f - (0.97239411f - 0.19194795f * z * z) * z;
                }
            }
        }
        else
        {
            if (y > 0.0f) // x = 0, y > 0
            {
                return 1.5707963267948966f;
            }
            else if (y < 0.0f) // x = 0, y < 0
            {
                return -1.5707964f;
            }
        }
        return 0.0f; // x,y = 0. Could return NaN instead.
    }

    public static float atan(final float v) {
        final float n = Math.abs(v);
        final float c = (n - 1f) / (n + 1f);
        final float c2 = c * c;
        final float c3 = c * c2;
        final float c5 = c3 * c2;
        final float c7 = c5 * c2;
        return Math.copySign(0.7853981633974483f +
                (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7), v);
    }

    public static float atan2General(float y, float x) {
        if(x > 0) {
            if(y >= 0){
                final float n = y / x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return 0.7853981633974483f +
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
            else {
                final float n = y / -x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return -0.7853981633974483f -
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
        } else if(x < 0) {
            if (y >= 0) {
                final float n = y / -x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return 2.356194490192345f -
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            } else {
                final float n = y / x;
                final float c = (n - 1f) / (n + 1f);
                final float c2 = c * c;
                final float c3 = c * c2;
                final float c5 = c3 * c2;
                final float c7 = c5 * c2;
                return -2.356194490192345f +
                        (0.999215f * c - 0.3211819f * c3 + 0.1462766f * c5 - 0.0389929f * c7);
            }
        }
        else return Math.copySign(1.5707963267948966f, y);
    }
}
