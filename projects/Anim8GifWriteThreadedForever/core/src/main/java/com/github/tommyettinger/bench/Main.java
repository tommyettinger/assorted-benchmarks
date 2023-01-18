package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.AnimatedGif;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PaletteReducer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Running for 64 iterations of Neue on Java 19:
 * <pre>
 *     Took 147059 ms to write 64 GIFs.
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    AnimatedGif gif;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;

    @Override
    public void create() {
        Logger.getGlobal().setLevel(Level.WARNING);
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        gif = new AnimatedGif();
        pixmaps = new Array<>(true, 90, Pixmap.class);
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        for (int i = 1; i <= 90; i++) {
            pixmaps.add(new Pixmap(root.child(name + "/" + name + "_" + i + ".jpg")));
        }
        gif.setPalette(new PaletteReducer());
        gif.setFlipY(true);

        gif.setDitherAlgorithm(Dithered.DitherAlgorithm.NEUE);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 64 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " GIFs.");
            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        gif.write(Gdx.files.local("tmp/imagesClean/" + name + "/AnimatedGif-" + name + "-Scatter.gif"), pixmaps, fps + (numWritten & 7));
        numWritten++;
    }
}
