package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectMapChanging;
import com.github.tommyettinger.ds.ObjectMapDebug;
import de.heidelberg.pvs.container_bench.generators.dictionary.StringDictionaryGenerator;
import org.junit.Test;

import java.io.IOException;

public class MapCollisionTest {
    @Test
    public void testObjectMapDebug() {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        try {
            final int LEN = 100000;
            gen.init(LEN, 1);
            String[] strings = gen.generateArray(LEN);;
            ObjectMapDebug<String, String> dict = new ObjectMapDebug<>();
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], null);
            }
        } catch (IOException ignored) {
        }
    }
    @Test
    public void testObjectMapChanging() {
        StringDictionaryGenerator gen = new StringDictionaryGenerator();
        try {
            final int LEN = 100000;
            gen.init(LEN, 1);
            String[] strings = gen.generateArray(LEN);;
            ObjectMapChanging<String, String> dict = new ObjectMapChanging<>();
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], null);
            }
        } catch (IOException ignored) {
        }
    }
}
