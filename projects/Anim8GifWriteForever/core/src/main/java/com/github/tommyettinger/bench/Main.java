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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private static final String name = "market";
    AnimatedGif gif;
    Array<Pixmap> pixmaps;
    int numWritten = 0;
    int fps = 17;
    long startTime;

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

        gif.setDitherAlgorithm(Dithered.DitherAlgorithm.SCATTER);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " GIFs.");
            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        gif.write(Gdx.files.local("tmp/imagesClean/" + name + "/AnimatedGif-" + name + "-Scatter.gif"), pixmaps, fps + (numWritten & 7));
        numWritten++;
    }
}
