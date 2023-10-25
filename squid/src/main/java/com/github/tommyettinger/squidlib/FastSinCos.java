package com.github.tommyettinger.squidlib;

/**
 * A very-quickly-made lookup table for integer degrees and their corresponding sin/cos results.
 * This is slightly faster than MathUtils.cosDeg() for int inputs, and slightly slower than
 * MathUtils.sinDeg() for those same inputs. The difference is in single-digit nanoseconds, though,
 * as is the total time per call.
 * <br>
 * A big thing to take away from this is that having separate sin and cos tables makes cos() a
 * little faster, and could be more precise for float inputs.
 * <br>
 * The original code is by Antz, and minor changes were made here so that it doesn't crash on
 * negative inputs.
 */
public class FastSinCos {
    public final float[] cos = new float[721];
    public final float[] sin = new float[721];
    private static final FastSinCos instance = new FastSinCos();

    private FastSinCos() {
        for (int i = 0; i <= 360; i++) {
            cos[i + 360] = cos[i] = (float) Math.cos(Math.toRadians(i));
            sin[i + 360] = sin[i] = (float) Math.sin(Math.toRadians(i));
        }
    }

    public float sinDeg(int angle) {
        int angleCircle = angle % 360 + 360;
        return sin[angleCircle];
    }

    public float cosDeg(int angle) {
        int angleCircle = angle % 360 + 360;
        return cos[angleCircle];
    }

    public static FastSinCos getTable() {
        return instance;
    }
}