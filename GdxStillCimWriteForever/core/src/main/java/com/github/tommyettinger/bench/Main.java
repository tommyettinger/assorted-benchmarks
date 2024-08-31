package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Running for 100 iterations on Java 19:
 * <pre>
 *     //// cat.jpg
 *     Took 8538 ms to write 100 CIMs
 *     Image is 1816104 bytes in size.
 *     //// ColorGuard.png
 *     Took 94175 ms to write 100 CIMs
 *     Image is 4864251 bytes in size.
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String[] names = {"cat", "ColorGuard", };
    private static final String[] extensions = {".jpg", ".png", };

    String name;
    String extension;

    Pixmap pixmap;
    int numWritten = 0;
    long startTime;

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
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        pixmap = new Pixmap(root.child(name + "/" + name + extension));

        startTime = TimeUtils.millis();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        FileHandle target = Gdx.files.local("tmp/imagesClean/" + name + "/Cim-" + name + ".cim");
        if(numWritten == 2 || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            System.out.println("Took " + (TimeUtils.millis() - startTime) + " ms to write " + numWritten + " CIMs");
//            Gdx.files.local("tmp/imagesClean").deleteDirectory();
            System.out.println("Image is " + target.length() + " bytes in size.");
            Gdx.app.exit();
        }
        PixmapIO.writeCIM(target, pixmap);
        numWritten++;
    }
}
