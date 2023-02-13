package com.github.tommyettinger.bench;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.Apng;

/**
 * Running for 100 iterations on Java 19:
 * <pre>
 *     Took 11617 ms to write 100 PNGs
 * </pre>
 */
public class Main extends ApplicationAdapter {
    private static final String name = "cat";
    private static final String INPUT_EXTENSION = ".jpg";

    Apng png;
    Pixmap pixmap;
    int numWritten = 0;
    long startTime;

    public Main(String algorithm) {
    }

    @Override
    public void create() {
        Gdx.files.local("tmp/imagesClean").mkdirs();
        Gdx.files.local("tmp/imagesClean").deleteDirectory();
        png = new Apng();
        FileHandle root = Gdx.files.local("SharedAssets/");
        if(!root.exists()) root = Gdx.files.local("../SharedAssets");
        if(!root.exists()) root = Gdx.files.local("../../SharedAssets");
        pixmap = new Pixmap(root.child(name + "/" + name + INPUT_EXTENSION));
        png.setFlipY(true); // the default is also true

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
        png.write(Gdx.files.local("tmp/imagesClean/" + name + "/Apng-" + name + ".png"), pixmap);
        numWritten++;
    }
}
