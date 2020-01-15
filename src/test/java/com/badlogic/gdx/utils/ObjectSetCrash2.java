package com.badlogic.gdx.utils;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

public class ObjectSetCrash2 extends ApplicationAdapter {
    private static final String[] problemStrings = 
          ("l1nqp kPoRp jpOqp jpOs2 jop3p l2Os2 l2Q4Q l2Q3p l2PSQ kR0rQ l1p3p l1nrQ l2OrQ kQPRp"
        + " d7PoU d7R1U d6ooU d6q26 d8326 cVR1U d81oU buR26 bv1oU cUq26 cUop6 d7QOt cW30t"
        + " JvS20 KWQp0 L92oO Jur1O KVqQ0 Jw2nn L940n KWRPO JuqQ0 Jupnn JvS0n JvQp0"
        + " OX5pT OVtPs Nw735 OVtR5 OX5os Nw72T Nuu35 OVspT P7spT OX6Ps P7u2T P8TpT"
        + " TQ8vw U1XY9 Sp9Y9 TOx8w SoWwX Snx8w U1XXX U1XWw U1WwX SoY9X U1Y8w U29Y9"
        + " ETtR- EV5ok DtTok Dsu1k Dsu3- ETu3- EUV2L F6UPk Du73- DtUR- Du5q- Du71k"
        + " JLu64 JN6Sr Im74r Im75S JN5sS JMUU4 K-ssS IlV5S Im6TS Im5sS Iku4r"
        + " PTvRS Osupr OsvQr Q77pr Q6WS4 PV7pr Ou8RS Ou7qS OsvS4 OtVpr PTw44"
        + " RXP90 RWnWO RY17n Qx0Vn S8nX0 S9OX0 RXNvO RY0X0 RY0WO RY0Vn S8mvO"
        + " X1v65 Vq7TT X2VSs X2Ut5 X36rs WR7U5 Vq85T Vq6rs X1uTT VouSs WPuU5"
        + " ko498 knS98 lNr98 lOQvW ko3X8 knQuv knQw8 lNpuv lNpvW kmpvW ko3Vv"
        + " q3t4r pRt5S os4sS q4U5S q3t64 oqrt4 q3sU4 q54rr q55U4 pRt64 pRrrr"
        + " xRPo8 y3PnW xQonW xRPnW xQq18 xRR18 xS30W wr2P8 wqQNv xQomv y42Nv"
        + " jRuw7 k3vVu irX7u iqvWV iqvVu k4Vw7 k4X8V irVuu iqvX7 k3uuu jSVuu"
        + " 2fV81 2fUVP 3FtUo 2fV6o 3GUW1 2esv1 3H781 3GV7P 2fUW1 2g781 3FsuP"
        + " 3T6S1 44US1 456S1 44V41 2s6S1 2s5r1 2rUQo 3Rspo 44URP 3SURP 43spo").split(" ");
    private ObjectSet<String> theSet;
    @Override
    public void create() {
        theSet = new ObjectSet<>(problemStrings.length);
//        theMap = new ObjectSet<>(width * height, 0.5f);
        generate();
    }
    
    public void generate()
    {
        final long startTime = TimeUtils.nanoTime();
        int stashCache = theSet.stashSize;
        for (int x = 0; x < problemStrings.length; x++) {
            if (theSet.stashSize > stashCache) {
                stashCache = theSet.stashSize;
                System.out.println("size: " + theSet.size + ", stash size: " + stashCache + ", capacity: " + theSet.capacity);
            }
            theSet.add(problemStrings[x]);
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
        new Lwjgl3Application(new ObjectSetCrash2(), config);
    }
}