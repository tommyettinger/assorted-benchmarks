package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;

/**
 * Running for 100 iterations on Java 19:
 * <pre>
 *     //// cat.jpg
 *     Took 27363 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 130362 ms to write 100 PNGs
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "cat";
    private static final String INPUT_EXTENSION = ".jpg";
//    private static final String name = "ColorGuard";
//    private static final String INPUT_EXTENSION = ".png";

    PixmapIO.PNG png;
    Pixmap pixmap;
    int numWritten = 0;
    long startTime;

    public Main(String algorithm) {
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        png = new PixmapIO.PNG();
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        pixmap = new Pixmap(root.child(name + "/" + name + INPUT_EXTENSION));
        png.setFlipY(true); // the default is also true
        png.setCompression(6); // default compression used by other PNG stuff

        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 100 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " PNGs");
//            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            Gdx.app.exit();
        }
        try {
            png.write(Gdx.files.local("tmp/imagesClean/" + name + "/Png-" + name + ".png"), pixmap);
        } catch (IOException e) {
            throw new GdxRuntimeException("Whoops.");
        }
        numWritten++;
    }
}
