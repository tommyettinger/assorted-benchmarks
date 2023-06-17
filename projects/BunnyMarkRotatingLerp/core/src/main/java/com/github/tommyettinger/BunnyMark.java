package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Spritz;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Originally from https://github.com/staff0rd/bunnymark-libgdx/blob/master/core/src/in/atqu/bunnymark/BunnyMark.java
 * That was originally from https://github.com/johngirvin/bunnymark-libgdx/blob/master/core/src/com/nivrig/BunnyMark.java
 */
public class BunnyMark extends ApplicationAdapter {
    private SpriteBatch batch;
    private Spritz sprite;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private static final RandomXS128 random = new RandomXS128();
    private static float minX = 0;
    private static float maxX = 0;
    private static float minY = 0;
    private static float maxY = 0;

    private BitmapFont labelFont;
    private float labelY;
    private final StringBuilder bunnyLabel = new StringBuilder(7);
    private final StringBuilder fpsLabel = new StringBuilder(9);
    private final StringBuilder sizeLabel = new StringBuilder(32);

    private static class Bunny {
        float x, y, speedX, speedY, angle;
        float tint;
        public Bunny(float tint) {
            this.tint = tint;
            angle = random.nextFloat() * 360f;
            y = maxY * 0.5f;
            x = 10;
            speedY = random.nextFloat() * 500f + 250f;
            speedX = random.nextFloat() * 500f - 250f;
        }
    }

    private final Array<Bunny> bunnies = new Array<Bunny>();

    @Override
    public void create() {

        batch = new SpriteBatch(5400);
        labelFont = new BitmapFont();
        labelFont.setColor(Color.WHITE);
        sprite = new Spritz(new Texture("wabbit_alpha.png"));
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        color = Color.WHITE_FLOAT_BITS;
        bunnies.add(new Bunny(color));
        bunnies.add(new Bunny(color));
        bunnyLabel.append('2');
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
//        if (Gdx.app.getType() != Application.ApplicationType.Desktop && Gdx.app.getType() != Application.ApplicationType.WebGL)
//            viewport.setUnitsPerPixel(1/Gdx.graphics.getDensity());
        maxX = camera.viewportWidth - sprite.getWidth();
        maxY = camera.viewportHeight - sprite.getHeight();
        labelY = camera.viewportHeight - 5;
        // String.format not available in GWT
        sizeLabel.setLength(0);
        sizeLabel.append(width).append('x').append(height).append('(').append(Math.round(camera.viewportWidth))
                .append('x').append(Math.round(camera.viewportHeight)).append(") ").append(Gdx.graphics.getDensity());
    }

    private void renderBunny(Bunny bunny, float dt) {
        bunny.x += dt * bunny.speedX;
        if (bunny.x < minX) { bunny.x = minX; bunny.speedX = -bunny.speedX; }
        else if (bunny.x > maxX) { bunny.x = maxX; bunny.speedX = -bunny.speedX; }

        bunny.y += dt * bunny.speedY;
        if (bunny.y < minY) { bunny.y = minY; bunny.speedY = -bunny.speedY; }
        else if (bunny.y > maxY) { bunny.y = maxY; bunny.speedY = -bunny.speedY; }
        
        bunny.angle += dt;

        sprite.setPosition(bunny.x,bunny.y);
        sprite.setPackedColor(bunny.tint);
        sprite.setRotation(bunny.angle);
        sprite.draw(batch);
    }

    private float fpsTime  = 0;
    private int   fpsCount = 0;
    private float color;
    @Override
    public void render() {

        if (Gdx.input.justTouched()) {
            color = NumberUtils.intToFloatColor(random.nextInt() | 0xFF000000);
            for (int i = 0; i < 1000; i++) {
                bunnies.add(new Bunny(color));
            }
            bunnyLabel.setLength(0);
            bunnyLabel.append(bunnies.size);
        }
        float dt = Gdx.graphics.getDeltaTime();

        fpsTime  += dt;
        fpsCount++;
        if (fpsTime > 1f) {
            fpsLabel.setLength(0);
            fpsLabel.append(fpsCount / fpsTime);
            fpsLabel.setLength(Math.min(fpsLabel.length(), 5));
            fpsLabel.append(" FPS");
            fpsTime  = 0;
            fpsCount = 0;
        }

        ScreenUtils.clear(Color.BLACK);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (int i = 0, c = bunnies.size; i < c; i++) {
            renderBunny(bunnies.get(i), dt);
        }

        float labelX = 5;
        labelFont.draw(batch, bunnyLabel, labelX, labelY);
        labelFont.draw(batch, fpsLabel  , labelX, labelY - 15);
        labelFont.draw(batch, sizeLabel, labelX, labelY - 30);

        batch.end();
    }
}
