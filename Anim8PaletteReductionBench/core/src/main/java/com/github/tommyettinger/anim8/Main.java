package com.github.tommyettinger.anim8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.Locale;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private Pixmap editing;
    private int seed = 123456789;
    private long nanoTimeTotal;
    @Override
    public void create() {
        editing = PixmapMaker.make(256, 256);
////Took       19,553,720,600 nanoseconds
////Took       23,457,996,900 nanoseconds
        PaletteReducer pr = new PaletteReducer(); //Knoll, float sorting network on RGBA colors
////Took       18,315,058,900 nanoseconds
////Took       22,931,361,200 nanoseconds
//        PaletteReducer2 pr = new PaletteReducer2(); // Knoll, float sorting network on palette indices
////Took       24,236,118,300 nanoseconds
//        PaletteReducer3 pr = new PaletteReducer3(); // Knoll, insertion sort on palette indices
////Took        1,781,769,900 nanoseconds
//        PaletteReducer2 pr = new PaletteReducer2(); // Oceanic
//Took       22,524,058,100 nanoseconds
//Took       21,715,316,100 nanoseconds
//        PaletteReducer4 pr = new PaletteReducer4(); // Knoll, int sorting network on palette indices
        for (int i = 0; i < 1000; i++) {
            PixmapMaker.alter(editing, seed *= 0x12312345);
            long startTime = System.nanoTime();
            pr.reduceKnoll(editing);
//            pr.reduceOceanic(editing);
            nanoTimeTotal += System.nanoTime() - startTime;
        }
        System.out.printf(Locale.US, "Took % ,20d nanoseconds", nanoTimeTotal);
        Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }
}
