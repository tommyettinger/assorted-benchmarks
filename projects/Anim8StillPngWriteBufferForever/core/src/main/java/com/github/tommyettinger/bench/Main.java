package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Apng;

import java.io.ByteArrayOutputStream;

/**
 * WITH FILTER_NONE:
 * <br>
 * Running for 100 iterations on Java 19 with compression 6, writing to disk:
 * <pre>
 *     //// cat.jpg
 *     Took 11617 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 90845 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 2, writing to disk:
 * <pre>
 *     //// cat.jpg
 *     Took 6732 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 27685 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 0, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 1691 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 3102 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 1, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 5913 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 24502 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 2, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 6552 ms to write 100 PNG
 *     //// ColorGuard.png
 *     Took 27419 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 3, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 7246 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 37179 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 4, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 8261 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 38500 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 5, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 10596 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 55550 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 6, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11460 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 91845 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 7, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11451 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 120597 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 8, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11570 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 229390 ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 9, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 11469 ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took 299168 ms to write 100 PNGs
 * </pre>
 * <br>
 * WITH FILTER_PAETH:
 * <br>
 * Running for 100 iterations on Java 19 with compression 6, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 22374 ms to write 100 PNGs
 *     Image is 1476145 bytes in size.
 *     //// ColorGuard.png
 *     Took 122772 ms to write 100 PNGs
 *     Image is 6202379 bytes in size.
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String[] names = {"cat", "ColorGuard", };
    private static final String[] extensions = {".jpg", ".png", };

    String name;
    String extension;
    Apng png;
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
        png = new Apng();
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        pixmap = new Pixmap(root.child(name + "/" + name + extension));
        png.setFlipY(true); // the default is also true
        png.setCompression(compression); // lower than default compression rate, faster
        baos = new ByteArrayOutputStream(0x800000);
        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(numWritten == 2 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " PNGs.");
//            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            System.out.println("Image is " + baos.size() + " bytes in size.");
            Gdx.app.exit();
        }
        baos.reset();
        png.write(baos, pixmap);
//        png.write(Gdx.files.local("tmp/imagesClean/" + name + "/Png-" + name + ".png"), pixmap);
        numWritten++;
    }
}
