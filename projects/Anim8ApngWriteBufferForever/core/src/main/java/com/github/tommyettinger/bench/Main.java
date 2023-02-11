package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Apng;

/**
 * Running for 32 iterations on Java 19:
 * <pre>
 *     Took 97003 ms to write 32 APNGs
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    private static final int TOTAL_FRAMES = 90;
    private static final String INPUT_EXTENSION = ".jpg";
//    private static final String name = "flashy"; // "market";
//    private static final int TOTAL_FRAMES = 80; // 90 for market
//    private static final String INPUT_EXTENSION = ".png"; // ".jpg";
    Apng apng;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;

    public Main(String algorithm) {
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        apng = new Apng();
        pixmaps = new Array<>(true, TOTAL_FRAMES, Pixmap.class);
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        for (int i = 1; i <= TOTAL_FRAMES; i++) {
            pixmaps.add(new Pixmap(root.child(name + "/" + name + "_" + i + INPUT_EXTENSION)));
        }
        apng.setFlipY(true); // the default is also true

        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 32 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " APNGs");
            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        apng.write(Gdx.files.local("tmp/imagesClean/" + name + "/Apng-" + name + ".png"), pixmaps, fps + (numWritten & 7));
        numWritten++;
    }
}
