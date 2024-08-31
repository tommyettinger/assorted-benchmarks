package com.github.tommyettinger.gwt;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.github.tommyettinger.BunnyMark;

/** Launches the GWT application. */
public class GwtLauncher extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig () {
        // Resizable application, uses available space in browser with no padding:
//			GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(true);
//			cfg.padVertical = 0;
//			cfg.padHorizontal = 0;
//			return cfg;
        // If you want a fixed size application, use this instead:
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(640, 480);
        cfg.disableAudio = true;
        return cfg;
    }

    @Override
    public ApplicationListener createApplicationListener () {
        return new BunnyMark();
    }
}
