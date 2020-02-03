package com.badlogic.gdx.utils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;

public class ObjectSetCrash4 extends ApplicationAdapter {
    private static final int width = 24, height = 24;
    private ObjectSet<Vector2> theSet;
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
        int stashCache = theSet.stashSize;
        for (int x = -halfWidth; x < halfWidth; x++) {
            for (int y = -halfHeight; y < halfHeight; y++) {
                if(theSet.stashSize > stashCache)
                {
                    stashCache = theSet.stashSize;
                    System.out.println("size: " + theSet.size + ", stash size: " + stashCache + ", capacity: " + theSet.capacity);
                }
                theSet.add(new Vector2(x, y));
            }
        }
        long taken = TimeUtils.timeSinceNanos(startTime);
        System.out.println(taken + "ns taken, about 10 to the " + Math.log10(taken) + " power.");
    }

    @Override
    public void render() {
        // standard clear the background routine for libGDX
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("LibGDX Test: ObjectSet<GridPoint2> crash with width="+width+", height="+height);
        config.setWindowedMode(500, 100);
        config.setIdleFPS(1);
        new Lwjgl3Application(new ObjectSetCrash4(), config);
    }
}