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
 * Running for 100 iterations on Java 19 with compression 6, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took ??? ms to write 100 PNGs
 *     //// ColorGuard.png
 *     Took ??? ms to write 100 PNGs
 * </pre>
 * Running for 100 iterations on Java 19 with compression 2, memory-only:
 * <pre>
 *     //// cat.jpg
 *     Took 6506 ms to write 100 PNG
 *     //// ColorGuard.png
 *     Took 27176 ms to write 100 PNGs
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

    public Main(String algorithm) {
        try {
            int index = Integer.parseInt(algorithm);
            name = names[index];
            extension = extensions[index];
        } catch (Exception e) {
            name = names[0];
            extension = extensions[0];
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
        png.setCompression(2); // lower than default compression rate, faster
        baos = new ByteArrayOutputStream(0x800000);
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
        png.write(baos, pixmap);
        baos.reset();
//        png.write(Gdx.files.local("tmp/imagesClean/" + name + "/Png-" + name + ".png"), pixmap);
        numWritten++;
    }
}
