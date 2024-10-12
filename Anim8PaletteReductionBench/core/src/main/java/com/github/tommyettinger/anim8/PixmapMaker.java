package com.github.tommyettinger.anim8;

import com.badlogic.gdx.graphics.Pixmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class PixmapMaker {
    private PixmapMaker() {}
    public static Pixmap make(int w, int h){
        return new Pixmap(w, h, Pixmap.Format.RGBA8888);
    }
    public static Pixmap alter(Pixmap editing, int b) {
        final ByteBuffer pixels = editing.getPixels();
        final IntBuffer ib = pixels.asIntBuffer();
        final int lim = ib.limit();
        for (int i = 0; i < lim; i++) {
            ib.put(
                (b = (b = ((b = (++b ^ b >>> 16) * 0x21f0aaad) ^ b >>> 15) * 0x735a2d97) ^ b >>> 15)
                    | 0xFF);
        }
        return editing;
    }
}
