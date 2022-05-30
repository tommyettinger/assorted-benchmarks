package de.heidelberg.pvs.container_bench;

import com.github.tommyettinger.ds.ObjectMapChanging;
import com.github.tommyettinger.ds.ObjectMapDebug;
import de.heidelberg.pvs.container_bench.generators.Wordlist;
import org.junit.Test;

import java.io.IOException;

public class MapCollisionTest {
    @Test
    public void testObjectMapDebug() {
        try {
            final int LEN = 5000000;
            String[] strings = Wordlist.loadUniqueWords(LEN, 1).toArray(new String[0]);
            long start = System.nanoTime();
            ObjectMapDebug<String, String> dict = new ObjectMapDebug<>(51, 0.95f);
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], null);
            }
            System.out.println(System.nanoTime() - start);
            // with LEN=100000
            //112455700
            //103901600
            //144629900

            // with LEN=5000000
            // over 77 minutes without finishing.
            // Longest pileup was 578509, total collisions overflowed an int many times.
        } catch (IOException ignored) {
        }
    }
    @Test
    public void testObjectMapChanging() {
        try {
            final int LEN = 5000000;
            String[] strings = Wordlist.loadUniqueWords(LEN, 1).toArray(new String[0]);
            long start = System.nanoTime();
            ObjectMapChanging<String, String> dict = new ObjectMapChanging<>(51, 0.95f);
            for (int i = 0; i < LEN; i++) {
                dict.put(strings[i], null);
            }
            System.out.println(System.nanoTime() - start);
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
        } catch (IOException ignored) {
        }
    }
}
