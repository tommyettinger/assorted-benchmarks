package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Gif;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PaletteReducer;

/**
 * Running for 32 iterations on Java 19:
 * <pre>
 *     Took 35241 ms to write 32 GIFs using DIFFUSION
 *     Took 25606 ms to write 32 GIFs using CHAOTIC_NOISE
 *     Took 26400 ms to write 32 GIFs using ROBERTS
 *     Took 25970 ms to write 32 GIFs using GRADIENT_NOISE
 *     Took 13963 ms to write 32 GIFs using NONE
 *     Took 225147 ms to write 32 GIFs using PATTERN
 *     Took 40094 ms to write 32 GIFs using NEUE
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    private static final int TOTAL_FRAMES = 90;
    private static final String INPUT_EXTENSION = ".jpg";
//    private static final String name = "flashy"; // "market";
//    private static final int TOTAL_FRAMES = 80; // 90 for market
//    private static final String INPUT_EXTENSION = ".png"; // ".jpg";
    Gif gif;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;
    Dithered.DitherAlgorithm dither = Dithered.DitherAlgorithm.GRADIENT_NOISE;

    public Main(String algorithm) {
        if (!"".equals(algorithm)) {
            try {
                dither = Dithered.DitherAlgorithm.valueOf(algorithm);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid algorithm. Valid choices are:");
                System.out.println(Dithered.DitherAlgorithm.NONE);
                System.out.println(Dithered.DitherAlgorithm.GRADIENT_NOISE);
                System.out.println(Dithered.DitherAlgorithm.PATTERN);
                System.out.println(Dithered.DitherAlgorithm.CHAOTIC_NOISE);
                System.out.println(Dithered.DitherAlgorithm.NEUE);
                System.out.println(Dithered.DitherAlgorithm.DIFFUSION);
                System.out.println(Dithered.DitherAlgorithm.ROBERTS);
                System.out.println(Dithered.DitherAlgorithm.SCATTER);
                System.out.println(Dithered.DitherAlgorithm.BLUE_NOISE);
                System.out.println(Dithered.DitherAlgorithm.WOVEN);
//                for (Dithered.DitherAlgorithm d : Dithered.DitherAlgorithm.values()) {
//                    System.out.println(d.name());
//                }
                System.exit(1);
            }
        }
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        gif = new Gif();
        pixmaps = new Array<>(true, TOTAL_FRAMES, Pixmap.class);
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        for (int i = 1; i <= TOTAL_FRAMES; i++) {
            pixmaps.add(new Pixmap(root.child(name + "/" + name + "_" + i + INPUT_EXTENSION)));
        }
        gif.setPalette(new PaletteReducer());
        gif.setFlipY(true); // the default is also true

        gif.setDitherAlgorithm(dither);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 32 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " GIFs using " + dither.name());
            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        gif.write(Gdx.files.local("tmp/imagesClean/" + name + "/Gif-" + name + "-" + dither.name() + ".gif"), pixmaps, fps + (numWritten & 7));
        numWritten++;
    }
}
