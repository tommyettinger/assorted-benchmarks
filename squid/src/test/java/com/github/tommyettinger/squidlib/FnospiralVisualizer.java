package com.github.tommyettinger.squidlib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import squidpony.squidmath.*;

import static com.badlogic.gdx.Input.Keys.*;
import static com.badlogic.gdx.graphics.GL20.GL_POINTS;

/**
 */
public class FnospiralVisualizer extends ApplicationAdapter {
    private Fnospiral noise = new Fnospiral(1, 0.0625f, FastNoise.SIMPLEX_FRACTAL, 1);
    private int dim; // this can be 0, 1, 2, or 3; correspond to 2D, 3D, 4D, or 6D
    private int octaves = 3;
    private float freq = 0.125f;
    private boolean inverse;
    private ImmediateModeRenderer20 renderer;
    
    private static final int width = 512, height = 512;

    private InputAdapter input;

    private Viewport view;
    private int ctr = -256;
    private boolean keepGoing = true;

    public static float basicPrepare(float n)
    {
        return n * 0.5f + 0.5f;
    }

    @Override
    public void create() {
        renderer = new ImmediateModeRenderer20(width * height, false, true, 0);
        view = new ScreenViewport();
        noise.setFractalType(FastNoise.FBM);

        input = new InputAdapter(){
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case P: //pause
                        keepGoing = !keepGoing;
                    case C:
                        ctr++;
                        break;
                    case ENTER:
                    case N: // noise type
                    case EQUALS:
                        noise.setNoiseType((noise.getNoiseType() + 1) % 12);
                        break;
                    case MINUS:
                        noise.setNoiseType((noise.getNoiseType() + 11) % 12);
                        break;
                    case D: //dimension
                        dim = (dim + 1) & 3;
                        break;
                    case F: // frequency
//                        noise.setFrequency(NumberTools.sin(freq += 0.125f) * 0.25f + 0.25f + 0x1p-7f);
                        noise.setFrequency((float) Math.pow(2f, (System.currentTimeMillis() >>> 9 & 7) - 5));
                        break;
                    case R: // fRactal type
                        noise.setFractalType((noise.getFractalType() + 1) % 3);
                        break;
                    case H: // higher octaves
                        noise.setFractalOctaves((octaves = octaves + 1 & 7) + 1);
                        break;
                    case L: // lower octaves
                        noise.setFractalOctaves((octaves = octaves + 7 & 7) + 1);
                        break;
                    case I: // inverse mode
                        if (inverse = !inverse) {
                            noise.setFractalLacunarity(0.5f);
                            noise.setFractalGain(2f);
                        } else {
                            noise.setFractalLacunarity(2f);
                            noise.setFractalGain(0.5f);
                        }
                        break;
                    case K: // sKip
                        ctr += 1000;
                        break;
                    case Q:
                    case ESCAPE: {
                        Gdx.app.exit();
                    }
                }
                return true;
            }
        };
        Gdx.input.setInputProcessor(input);
    }

    public void putMap() {
        renderer.begin(view.getCamera().combined, GL_POINTS);
        float bright;
        switch (dim) {
            case 0:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bright = basicPrepare(noise.getConfiguredNoise(x + ctr, y + ctr));
                        renderer.color(bright, bright, bright, 1f);
                        renderer.vertex(x, y, 0);
                    }
                }
                break;
            case 1:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bright = basicPrepare(noise.getConfiguredNoise(x, y, ctr));
                        renderer.color(bright, bright, bright, 1f);
                        renderer.vertex(x, y, 0);
                    }
                }
                break;
            case 2:
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bright = basicPrepare(noise.getConfiguredNoise(x, y, ctr, -0.125f * (x + y + ctr)));
                        renderer.color(bright, bright, bright, 1f);
                        renderer.vertex(x, y, 0);
                    }
                }
                break;
            case 3: {
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bright = basicPrepare(noise.getConfiguredNoise(
                                (y * 0.6f + x * 0.4f) + ctr * 1.3f, (x * 0.6f - y * 0.4f) + ctr * 1.3f,
                                (x * 0.7f + y * 0.3f) - ctr * 1.3f, (y * 0.7f - x * 0.3f) - ctr * 1.3f,
                                (x * 0.35f - y * 0.25f) * 0.65f - (x * 0.25f + y * 0.35f) * 1.35f,
                                (y * 0.45f - x * 0.15f) * 0.75f - (y * 0.15f + x * 0.45f) * 1.25f));
                        renderer.color(bright, bright, bright, 1f);
                        renderer.vertex(x, y, 0);
                    }
                }
            }
            break;
        }
        renderer.end();

    }

    @Override
    public void render() {
        // not sure if this is always needed...
        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));
        if (keepGoing) {
            // standard clear the background routine for libGDX
            Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            ctr++;
            putMap();
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        view.update(width, height, true);
        view.apply(true);
    }

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("SquidLib Test: Noise Visualization");
        config.setWindowedMode(width, height);
        config.useVsync(false);
        config.setResizable(false);
        new Lwjgl3Application(new FnospiralVisualizer(), config);
    }
}
