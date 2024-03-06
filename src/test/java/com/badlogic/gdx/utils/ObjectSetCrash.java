package com.badlogic.gdx.utils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.GridPoint2;

public class ObjectSetCrash extends ApplicationAdapter {
    // sometimes, this test will narrowly pass with width=44; increase it a little and you will probably get a crash
    private static final int width = 44, height = 160;
    private ObjectSet<GridPoint2> theSet;
    @Override
    public void create() {
        theSet = new ObjectSet<>(width * height);
//        theMap = new ObjectSet<>(width * height, 0.5f);
        generate();
    }
    
    public void generate()
    {
        final long startTime = TimeUtils.nanoTime();
        final int halfWidth = width / 2, halfHeight = height / 2;
        for (int x = -halfWidth; x < halfWidth; x++) {
            for (int y = -halfHeight; y < halfHeight; y++) {
                theSet.add(new GridPoint2(x, y));
            }
        }
        long taken = TimeUtils.timeSinceNanos(startTime);
        System.out.println(taken + "ns taken, about 10 to the " + Math.log10(taken) + " power.");
    }

    @Override
    public void render() {
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    public static void main(String[] arg) {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        System.out.println("libGDX Test: ObjectSet<GridPoint2> crash with width="+width+", height="+height);
        new HeadlessApplication(new ObjectSetCrash(), config);
    }
}