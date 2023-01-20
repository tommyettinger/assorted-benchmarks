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

/**
 * Running for 64 iterations on Java 19:
 * <pre>
 *     Took 99847 ms to write 64 GIFs using NEUE
 *     Took 485885 ms to write 64 GIFs using PATTERN
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    AnimatedGif gif;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;
    Dithered.DitherAlgorithm dither = Dithered.DitherAlgorithm.NEUE;

    public Main(String algorithm) {
        try {
            dither = Dithered.DitherAlgorithm.valueOf(algorithm);
        } catch(IllegalArgumentException e) {
            System.out.println("Invalid algorithm. Valid choices are:");
            for(Dithered.DitherAlgorithm d : Dithered.DitherAlgorithm.values()) {
                System.out.println(d.name());
            }
            System.exit(1);
        }
    }

    @Override
    public void create() {
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

        gif.setDitherAlgorithm(dither);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 64 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " GIFs using " + dither.name());
            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        gif.write(Gdx.files.local("tmp/imagesClean/" + name + "/AnimatedGif-" + name + "-Scatter.gif"), pixmaps, fps + (numWritten & 7));
        numWritten++;
    }
}
