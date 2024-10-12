package com.github.tommyettinger.anim8;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainDebug extends ApplicationAdapter {
    private Pixmap editing;
    private int seed = 123456789;
    private Texture show;
    private SpriteBatch batch;
    @Override
    public void create() {
        batch = new SpriteBatch();
        editing = PixmapMaker.make(256, 256);
        PixmapMaker.alter(editing, seed *= 0x12312345);
        show = new Texture(editing);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)){
            PixmapMaker.alter(editing, seed *= 0x12312345);
            show.draw(editing, 0, 0);
        }
        batch.begin();
        batch.draw(show, 64, 64);;
        batch.end();
    }
}
