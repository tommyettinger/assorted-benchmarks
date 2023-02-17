package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PaletteReducer;
import com.github.tommyettinger.anim8.FastPNG8;

/**
 * Running for 32 iterations on Java 19:
 * <pre>
 *     // NONE filter, market
 *     Took 23663 ms to write 32 PNGs using NONE
 *     Took 50925 ms to write 32 PNGs using GRADIENT_NOISE
 *     Took 249653 ms to write 32 PNGs using PATTERN
 *     Took 54727 ms to write 32 PNGs using DIFFUSION
 *     Took 56564 ms to write 32 PNGs using BLUE_NOISE
 *     Took 56753 ms to write 32 PNGs using CHAOTIC_NOISE
 *     Took 62854 ms to write 32 PNGs using SCATTER
 *     Took 79308 ms to write 32 PNGs using NEUE
 *     Took 48343 ms to write 32 PNGs using ROBERTS
 *     Took 64055 ms to write 32 PNGs using WOVEN
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    private static final int TOTAL_FRAMES = 90;
    private static final String INPUT_EXTENSION = ".jpg";
//    private static final String name = "flashy"; // "market";
//    private static final int TOTAL_FRAMES = 80; // 90 for market
//    private static final String INPUT_EXTENSION = ".png"; // ".jpg";
//    private static final String name = "alpha";
//    private static final int TOTAL_FRAMES = 80;
//    private static final String INPUT_EXTENSION = ".png";
    FastPNG8 png;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;
    Dithered.DitherAlgorithm dither = Dithered.DitherAlgorithm.NONE;

    public Main(String algorithm) {
        if (!"".equals(algorithm)) {
            try {
                dither = Dithered.DitherAlgorithm.valueOf(algorithm);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid algorithm. Valid choices are:");
                for (Dithered.DitherAlgorithm d : Dithered.DitherAlgorithm.values()) {
                    System.out.println(d.name());
                }
                System.exit(1);
            }
        }
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        png = new FastPNG8();
        pixmaps = new Array<>(true, TOTAL_FRAMES, Pixmap.class);
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        for (int i = 1; i <= TOTAL_FRAMES; i++) {
            pixmaps.add(new Pixmap(root.child(name + "/" + name + "_" + i + INPUT_EXTENSION)));
        }
        png.setPalette(new PaletteReducer());
        png.setFlipY(true); // the default is also true

        png.setDitherAlgorithm(dither);

        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if (numWritten == 2 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " PNGs using " + dither.name());
//            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        } else {
            png.write(Gdx.files.local("tmp/imagesClean/" + name + "/FastPNG8-" + name + "-" + dither.name() + ".png"), pixmaps, fps + (numWritten & 7));
            numWritten++;
        }
    }
}
