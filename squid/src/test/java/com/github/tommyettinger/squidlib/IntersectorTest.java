package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import org.junit.Assert;
import org.junit.Test;

public class IntersectorTest {
    @Test(expected = AssertionError.class)
    public void testIntersectorCurrent() {
        Assert.assertFalse(Intersector.isPointInTriangle(
                new Vector3(-5120.8345f, 8946.126f, -3270.5813f),
                new Vector3(50.008057f, 22.20586f, 124.62208f),
                new Vector3(62.282288f, 22.205864f, 109.665924f),
                new Vector3(70.92052f, 7.205861f, 115.437805f)));
    }

    // These are declared in Intersector, and can be used by any code put into that class.
    private final static Vector3 v0 = new Vector3();
    private final static Vector3 v1 = new Vector3();
    private final static Vector3 v2 = new Vector3();

    public static boolean isPointInTriangleTH(Vector3 point, Vector3 t1, Vector3 t2, Vector3 t3){
        v0.set(t1).sub(point);
        v1.set(t2).sub(point);
        v2.set(t3).sub(point);

        v1.crs(v2);
        v2.crs(v0);

        if(v1.dot(v2) < 0f) return false;
        v0.crs(v2.set(t2).sub(point));
        return (v1.dot(v0) >= 0f);
    }

    @Test
    public void testIntersectorTH() {
        Assert.assertFalse(isPointInTriangleTH(
                new Vector3(-5120.8345f, 8946.126f, -3270.5813f),
                new Vector3(50.008057f, 22.20586f, 124.62208f),
                new Vector3(62.282288f, 22.205864f, 109.665924f),
                new Vector3(70.92052f, 7.205861f, 115.437805f)));
    }

    public static boolean isPointInTriangleTE (Vector3 point, Vector3 t1, Vector3 t2, Vector3 t3) {
        v0.set(t1).sub(point);
        v1.set(t2).sub(point);
        v2.set(t3).sub(point);

        float ab = v0.dot(v1);
        float ac = v0.dot(v2);
        float bc = v1.dot(v2);
        float cc = v2.dot(v2);

        if (bc * ac < cc * ab) return false;
        float bb = v1.dot(v1);
        if (ab * bc < ac * bb) return false;
        return true;
    }

    @Test(expected = AssertionError.class)
    public void testIntersectorTE() {
        Assert.assertFalse(isPointInTriangleTE(
                new Vector3(-5120.8345f, 8946.126f, -3270.5813f),
                new Vector3(50.008057f, 22.20586f, 124.62208f),
                new Vector3(62.282288f, 22.205864f, 109.665924f),
                new Vector3(70.92052f, 7.205861f, 115.437805f)));
    }

}
