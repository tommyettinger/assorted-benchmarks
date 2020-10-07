package com.badlogic.gdx.utils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

public class ObjectSetCrash3 extends ApplicationAdapter {
    /**
     * Uses reference equality but has a broken hashCode() that always returns 0.
     */
    private static class Problem {
        public int hashCode(){
            return 0;
        }
    }
    private ObjectSet<Problem> theSet;
    @Override
    public void create() {
        theSet = new ObjectSet<>(256);
//        theMap = new ObjectSet<>(width * height, 0.5f);
        generate();
    }
    
    public void generate()
    {
        final long startTime = TimeUtils.nanoTime();
        for (int x = 0; x < 256; x++) {
            System.out.println("attempting to add element " + (theSet.size + 1));
            theSet.add(new Problem());
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
        config.setTitle("LibGDX Test: ObjectSet<String> crash");
        config.setWindowedMode(500, 100);
        config.setIdleFPS(1);
        new Lwjgl3Application(new ObjectSetCrash3(), config);
    }
}