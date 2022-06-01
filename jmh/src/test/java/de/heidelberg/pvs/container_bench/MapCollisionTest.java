package de.heidelberg.pvs.container_bench;

import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.ds.ObjectMapChanging;
import com.github.tommyettinger.ds.ObjectMapDebug;
import de.heidelberg.pvs.container_bench.generators.Wordlist;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class MapCollisionTest {
    public static final int LEN = 2000000;

    @Test
    public void testObjectMapDebug() {
        try {
            String[] strings = Wordlist.loadUniqueWords(LEN, 1).toArray(new String[0]);
            long start = System.nanoTime();
            ObjectMapDebug<String, Integer> dict = new ObjectMapDebug<>(51, 0.75f);
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], i);
            }
            System.out.println(System.nanoTime() - start);
            // with LEN=100000
            //112455700
            //103901600
            //144629900

            // with LEN=5000000, load factor 0.95f
            // over 77 minutes without finishing.
            // Longest pileup was 578509, total collisions overflowed an int many times.

            // with LEN=2000000, load factor 0.6f, 0.75f, or 0.95f
            // none of these were going to finish any time soon.
            // impossible-to-count collisions because they overflowed. very long pileups (> 80000).

        } catch (IOException ignored) {
        }
    }

    @Test
    public void testObjectMapDebugGenerated(){
        String[] strings = Wordlist.generateUniqueWords(LEN).toArray(new String[0]);
        long start = System.nanoTime();
        ObjectMapDebug<String, Integer> dict = new ObjectMapDebug<>(51, 0.75f);
        for (int i = 0; i < LEN; i++) {
            dict.put(strings[i], i);
        }
        System.out.println(System.nanoTime() - start);
    }

    @Test
    public void testObjectMapDebugVector2() {
        long start = System.nanoTime();
        ObjectMapDebug<Vector2, Integer> dict = new ObjectMapDebug<>(51, 0.75f);
        final int limit = (int)(Math.sqrt(LEN));
        for (int x = -limit; x < limit; x+=2) {
            for (int y = -limit; y < limit; y+=2) {
                dict.put(new Vector2(x, y), x);
            }
        }
        System.out.println(System.nanoTime() - start);

    }

    @Test
    public void testObjectMapChanging() {
        try {
            String[] strings = Wordlist.loadUniqueWords(LEN, 1).toArray(new String[0]);
            long start = System.nanoTime();
            ObjectMapChanging<String, Integer> dict = new ObjectMapChanging<>(51, 0.75f);
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], i);
            }
            System.out.println(System.nanoTime() - start);

            // verifying that the Map still behaves as intended.
            for (int i = 0; i < LEN; i++) {
                Assert.assertEquals((int) dict.get(strings[i]), i);
            }
            // with LEN=100000, load factor 0.95f
            //131243800
            //109143700
            //108626800

            // with LEN=5000000, load factor 0.95f
            //3574019000
            //3709378800
            // Longest pileup was 39, total collisions 1802604.

            // with LEN=5000000, load factor 0.75f
            //2253596500
            // Longest pileup was 23, total collisions 945069.

            // with LEN=5000000, load factor 0.6f
            //1973884500
            // Longest pileup was 17, total collisions 539578.

            // using simpler, more-random choice of randomMultiplier:

            // with LEN=5000000, load factor 0.95f
            //3749433800
            // Longest pileup was 36, total collisions 1809369.

            // with LEN=5000000, load factor 0.75f
            //2223402100
            // Longest pileup was 24, total collisions 944951.

            // with LEN=5000000, load factor 0.6f
            //1872310600
            // Longest pileup was 16, total collisions 540405.

            // with LEN=2000000, load factor 0.95f
            //1451849500
            // Longest pileup was 35, total collisions 899322.

            // with LEN=2000000, load factor 0.75f
            //763809100
            // Longest pileup was 29, total collisions 472681.

            // with LEN=2000000, load factor 0.6f
            //622316200
            // Longest pileup was 16, total collisions 269722.

        } catch (IOException ignored) {
        }
    }

    @Test
    public void testObjectMapChangingGenerated(){
        String[] strings = Wordlist.generateUniqueWords(LEN).toArray(new String[0]);
        long start = System.nanoTime();
        ObjectMapChanging<String, Integer> dict = new ObjectMapChanging<>(51, 0.75f);
        for (int i = 0; i < LEN; i++) {
            dict.put(strings[i], i);
        }
        System.out.println(System.nanoTime() - start);
    }

}
