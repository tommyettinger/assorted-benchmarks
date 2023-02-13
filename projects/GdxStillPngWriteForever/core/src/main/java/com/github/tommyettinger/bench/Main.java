package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Running for 100 iterations on Java 19 with compression 6, writing to disk:
 * <pre>
 *     //// cat.jpg
 *     Took 27363 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 130362 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 2, writing to disk:
 * <pre>
 *     //// cat.jpg
 *     Took 9053 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 45496 ms to write 100 PNG
 * </pre>
 * Running for 100 iterations on Java 19 with compression 0, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 3550 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 14664 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 1, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 8148 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 41034 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 2, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 8995 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 45150 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 3, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11767 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 58216 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 4, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11190 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 57467 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 5, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 16561 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 79021 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 6, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 28448 ms to write 100 PNGs
 *     Image is 1510262 bytes in size.
 *     //// ColorGuard.png
 *     Took 131515 ms to write 100 PNGs
 *     Image is 6194109 bytes in size.
 * </pre>
 * Running for 100 iterations on Java 19 with compression 7, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 34932 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 185323 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 8, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 67293 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 476937 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 9, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 97180 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 1238696 ms to write 100 PNGs
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String[] names = {"cat", "ColorGuard", };
    private static final String[] extensions = {".jpg", ".png", };

    String name;
    String extension;
    PixmapIO.PNG png;
    Pixmap pixmap;
    int numWritten = 0;
    long startTime;
    ByteArrayOutputStream baos;

    int compression;

    public Main(String input, String compression) {
        try {
            int index = Integer.parseInt(input);
            name = names[index];
            extension = extensions[index];
        } catch (Exception e) {
            name = names[0];
            extension = extensions[0];
        }
        try {
            this.compression = Integer.parseInt(compression);
        } catch (Exception e) {
            this.compression = 6;
        }
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        png = new PixmapIO.PNG();
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        pixmap = new Pixmap(root.child(name + "/" + name + extension));
        png.setFlipY(true); // the default is also true
        png.setCompression(compression);
        baos = new ByteArrayOutputStream(0x800000);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 2 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " PNGs");
//            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            System.out.println("Image is " + baos.size() + " bytes in size.");
            Gdx.app.exit();
        }
        baos.reset();
        try {
            png.write(baos, pixmap);
//            png.write(Gdx.files.local("tmp/imagesClean/" + name + "/Png-" + name + ".png"), pixmap);
        } catch (IOException e) {
            throw new GdxRuntimeException("Whoops");
        }
        numWritten++;
    }
}
