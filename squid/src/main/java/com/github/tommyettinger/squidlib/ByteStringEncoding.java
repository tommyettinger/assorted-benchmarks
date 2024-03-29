/*
 * Copyright (c) 2022 Eben Howard, Tommy Ettinger, and contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.tommyettinger.squidlib;

import com.github.tommyettinger.ds.ObjectIntMap;
import com.github.tommyettinger.ds.ObjectSet;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A variant on LZSEncoding to encode byte arrays to compressed Strings, and decode them back. This always uses
 * UTF-16-safe encoding, which means it does not use one bit of each char in the compressed Strings but makes
 * sure the Strings are valid UTF-16 (so they can be written to and read from file more safely).
 * <br>
 * Created by Tommy Ettinger on 1/11/2020.
 */
public final class ByteStringEncoding {
    private ByteStringEncoding(){}

    private static final String[] BYTE_STRINGS = new String[256];

    static {
        for (int i = 0; i < 256; i++) {
            BYTE_STRINGS[i] = Character.toString((char) i);
        }
    }

    public static String compress(byte[] uncompressed) {
        if (uncompressed == null) return null;
        if (uncompressed.length == 0) return "";
        final int bitsPerChar = 15, offset = 32;
        int i, value;
        HashMap<String, Integer> context_dictionary = new HashMap<>();
        HashSet<String> context_dictionaryToCreate = new HashSet<>();
        String context_c;
        String context_wc;
        String context_w = "";
        int context_enlargeIn = 2; // Compensate for the first entry which should not count
        int context_dictSize = 3;
        int context_numBits = 2;
        StringBuilder context_data = new StringBuilder(uncompressed.length >>> 1);
        int context_data_val = 0;
        int context_data_position = 0;
        int ii;

        for (ii = 0; ii < uncompressed.length; ii++) {
            context_c = BYTE_STRINGS[uncompressed[ii] & 255];
            if (!context_dictionary.containsKey(context_c)) {
                context_dictionary.put(context_c, context_dictSize++);
                context_dictionaryToCreate.add(context_c);
            }

            context_wc = context_w + context_c;
            if (context_dictionary.containsKey(context_wc)) {
                context_w = context_wc;
            } else {
                if (context_dictionaryToCreate.contains(context_w)) {
                    value = (context_w.charAt(0) & 255);
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                    }
                    for (i = 0; i < 8; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }
                    context_enlargeIn--;
                    if (context_enlargeIn == 0) {
                        context_enlargeIn = 1 << context_numBits++;
                    }
                    context_dictionaryToCreate.remove(context_w);
                } else {
                    value = context_dictionary.get(context_w);
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }

                }
                context_enlargeIn--;
                if (context_enlargeIn == 0) {
                    context_enlargeIn = 1 << context_numBits++;
                }
                // Add wc to the dictionary.
                context_dictionary.put(context_wc, context_dictSize++);
                context_w = context_c;
            }
        }

        // Output the code for w.
        if (!context_w.isEmpty()) {
            if (context_dictionaryToCreate.contains(context_w)) {
//                if (context_w.charAt(0) < 256) {
                for (i = 0; i < context_numBits; i++) {
                    context_data_val = (context_data_val << 1);
                    if (context_data_position == bitsPerChar - 1) {
                        context_data_position = 0;
                        context_data.append((char) (context_data_val + offset));
                        context_data_val = 0;
                    } else {
                        context_data_position++;
                    }
                }
                value = context_w.charAt(0);
                for (i = 0; i < 8; i++) {
                    context_data_val = (context_data_val << 1) | (value & 1);
                    if (context_data_position == bitsPerChar - 1) {
                        context_data_position = 0;
                        context_data.append((char) (context_data_val + offset));
                        context_data_val = 0;
                    } else {
                        context_data_position++;
                    }
                    value >>= 1;
                }
//                } else {
//                    value = 1;
//                    for (i = 0; i < context_numBits; i++) {
//                        context_data_val = (context_data_val << 1) | value;
//                        if (context_data_position == bitsPerChar - 1) {
//                            context_data_position = 0;
//                            context_data.append((char) (context_data_val + offset));
//                            context_data_val = 0;
//                        } else {
//                            context_data_position++;
//                        }
//                        value = 0;
//                    }
//                    value = context_w.charAt(0);
//                    for (i = 0; i < 16; i++) {
//                        context_data_val = (context_data_val << 1) | (value & 1);
//                        if (context_data_position == bitsPerChar - 1) {
//                            context_data_position = 0;
//                            context_data.append((char) (context_data_val + offset));
//                            context_data_val = 0;
//                        } else {
//                            context_data_position++;
//                        }
//                        value >>= 1;
//                    }
//                }

                context_dictionaryToCreate.remove(context_w);
            } else {
                value = context_dictionary.get(context_w);
                for (i = 0; i < context_numBits; i++) {
                    context_data_val = (context_data_val << 1) | (value & 1);
                    if (context_data_position == bitsPerChar - 1) {
                        context_data_position = 0;
                        context_data.append((char) (context_data_val + offset));
                        context_data_val = 0;
                    } else {
                        context_data_position++;
                    }
                    value >>= 1;
                }

            }
        }

        // Mark the end of the stream
        value = 2;
        for (i = 0; i < context_numBits; i++) {
            context_data_val = (context_data_val << 1) | (value & 1);
            if (context_data_position == bitsPerChar - 1) {
                context_data_position = 0;
                context_data.append((char) (context_data_val + offset));
                context_data_val = 0;
            } else {
                context_data_position++;
            }
            value >>= 1;
        }

        // Flush the last char
        while (true) {
            context_data_val = (context_data_val << 1);
            if (context_data_position == bitsPerChar - 1) {
                context_data.append((char) (context_data_val + offset));
                break;
            } else
                context_data_position++;
        }
        context_data.append(' ');
        return context_data.toString();
    }

    public static byte[] decompress(String compressed) {
        if (compressed == null)
            return null;
        if (compressed.isEmpty())
            return new byte[0];
        final char[] getNextValue = compressed.toCharArray();
        final int length = getNextValue.length, resetValue = 16384, offset = -32;
        ArrayList<String> dictionary = new ArrayList<>();
        int enlargeIn = 4, dictSize = 4, numBits = 3, position = resetValue, index = 1, resb, maxpower, power,
                resultLength = 0;
        String entry, w, c;
        ArrayList<String> result = new ArrayList<>();
        char bits, val = (char) (getNextValue[0] + offset);

        for (char i = 0; i < 3; i++) {
            dictionary.add(i, String.valueOf(i));
        }

        bits = 0;
        maxpower = 2;
        power = 0;
        while (power != maxpower) {
            resb = val & position;
            position >>= 1;
            if (position == 0) {
                position = resetValue;
                val = (char) (getNextValue[index++] + offset);
            }
            bits |= (resb > 0 ? 1 : 0) << power++;
        }

        switch (bits) {
            case 0:
                bits = 0;
                maxpower = 8;
                power = 0;
                while (power != maxpower) {
                    resb = val & position;
                    position >>= 1;
                    if (position == 0) {
                        position = resetValue;
                        val = (char) (getNextValue[index++] + offset);
                    }
                    bits |= (resb > 0 ? 1 : 0) << power++;
                }
                c = String.valueOf(bits);
                break;
            case 1:
                bits = 0;
                maxpower = 16;
                power = 0;
                while (power != maxpower) {
                    resb = val & position;
                    position >>= 1;
                    if (position == 0) {
                        position = resetValue;
                        val = (char) (getNextValue[index++] + offset);
                    }
                    bits |= (resb > 0 ? 1 : 0) << power++;
                }
                c = String.valueOf(bits);
                break;
            default:
                return new byte[0];
        }
        dictionary.add(c);
        w = c;
        result.add(w);
        resultLength += w.length();
        while (true) {
            if (index > length) {
                return new byte[0];
            }
            int cc = 0;
            maxpower = numBits;
            power = 0;
            while (power != maxpower) {
                resb = val & position;
                position >>= 1;
                if (position == 0) {
                    position = resetValue;
                    val = (char) (getNextValue[index++] + offset);
                }
                cc |= (resb > 0 ? 1 : 0) << power++;
            }
            switch (cc) {
                case 0:
                    bits = 0;
                    maxpower = 8;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (getNextValue[index++] + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }

                    dictionary.add(String.valueOf(bits));
                    cc = dictSize++;
                    enlargeIn--;
                    break;
                case 1:
                    bits = 0;
                    maxpower = 16;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (getNextValue[index++] + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }
                    dictionary.add(String.valueOf(bits));
                    cc = dictSize++;
                    enlargeIn--;
                    break;
                case 2:
                    StringBuilder sb = new StringBuilder(resultLength);
                    for (int i = 0, n = result.size(); i < n; i++) {
                        sb.append(result.get(i));
                    }
                    try {
                        return sb.toString().getBytes("ISO-8859-1");
                    } catch (UnsupportedEncodingException e) {
                        return null; // should never happen, unless you're deep in the crazy mines.
                    }
                    // this is a possible alternative, but StandardCharsets may add to startup time if loaded early.
//                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
            }

            if (enlargeIn == 0) {
                enlargeIn = 1 << numBits;
                numBits++;
            }

            if (cc < dictionary.size() && dictionary.get(cc) != null) {
                entry = dictionary.get(cc);
            } else {
                if (cc == dictSize) {
                    entry = w + w.charAt(0);
                } else {
                    return new byte[0];
                }
            }
            result.add(entry);
            resultLength += entry.length();

            // Add w+entry[0] to the dictionary.
            dictionary.add(w + entry.charAt(0));
            dictSize++;
            enlargeIn--;

            w = entry;

            if (enlargeIn == 0) {
                enlargeIn = 1 << numBits;
                numBits++;
            }

        }

    }

    public static final class StringIntMap extends ObjectIntMap<String> {
        public StringIntMap() {
            super();
        }

        public StringIntMap(int initialCapacity) {
            super(initialCapacity);
        }

        public StringIntMap(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        @Override
        protected int place(@NonNull Object item) {
            return item.hashCode() & mask;
        }
    }

    public static final class StringSet extends ObjectSet<String> {
        public StringSet() {
            super();
        }

        public StringSet(int initialCapacity) {
            super(initialCapacity);
        }

        public StringSet(int initialCapacity, float loadFactor) {
            super(initialCapacity, loadFactor);
        }

        @Override
        protected int place(@NonNull Object item) {
            return item.hashCode() & mask;
        }

    }

    //TODO: Figure out which optimizations to use.
    /**
     * Temporary class to compare optimizations against the regular ByteStringEncoding.
     */
    public static final class Opt {
        private static final String[] BYTE_STRINGS = new String[256];

        static {
            for (int i = 0; i < 256; i++) {
                BYTE_STRINGS[i] = Character.toString((char) i);
            }
        }

        public static String compress(byte[] uncompressed) {
            if (uncompressed == null) return null;
            if (uncompressed.length == 0) return "";
            final int bitsPerChar = 15, offset = 32;
            int i, value;
            HashMap<String, Integer> context_dictionary = new HashMap<>(256, 0.5f);
            HashSet<String> context_dictionaryToCreate = new HashSet<>(256, 0.5f);
            String context_c;
            String context_wc;
            String context_w = "";
            int context_enlargeIn = 2; // Compensate for the first entry which should not count
            int context_dictSize = 3;
            int context_numBits = 2;
            StringBuilder context_data = new StringBuilder(uncompressed.length >>> 1);
            int context_data_val = 0;
            int context_data_position = 0;
            int ii;

            for (ii = 0; ii < uncompressed.length; ii++) {
                context_c = BYTE_STRINGS[uncompressed[ii] & 255];
                if (!context_dictionary.containsKey(context_c)) {
                    context_dictionary.put(context_c, context_dictSize++);
                    context_dictionaryToCreate.add(context_c);
                }

                context_wc = context_w + context_c;
                if (context_dictionary.containsKey(context_wc)) {
                    context_w = context_wc;
                } else {
                    if (context_dictionaryToCreate.contains(context_w)) {
                        value = (context_w.charAt(0) & 255);
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                        }
                        for (i = 0; i < 8; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value >>= 1;
                        }
                        context_enlargeIn--;
                        if (context_enlargeIn == 0) {
                            context_enlargeIn = 1 << context_numBits++;
                        }
                        context_dictionaryToCreate.remove(context_w);
                    } else {
                        value = context_dictionary.get(context_w);
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                context_data.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value >>= 1;
                        }

                    }
                    context_enlargeIn--;
                    if (context_enlargeIn == 0) {
                        context_enlargeIn = 1 << context_numBits++;
                    }
                    // Add wc to the dictionary.
                    context_dictionary.put(context_wc, context_dictSize++);
                    context_w = context_c;
                }
            }

            // Output the code for w.
            if (!context_w.isEmpty()) {
                if (context_dictionaryToCreate.contains(context_w)) {
//                if (context_w.charAt(0) < 256) {
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                    }
                    value = context_w.charAt(0);
                    for (i = 0; i < 8; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }
                    context_dictionaryToCreate.remove(context_w);
                } else {
                    value = context_dictionary.get(context_w);
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            context_data.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }

                }
            }

            // Mark the end of the stream
            value = 2;
            for (i = 0; i < context_numBits; i++) {
                context_data_val = (context_data_val << 1) | (value & 1);
                if (context_data_position == bitsPerChar - 1) {
                    context_data_position = 0;
                    context_data.append((char) (context_data_val + offset));
                    context_data_val = 0;
                } else {
                    context_data_position++;
                }
                value >>= 1;
            }

            // Flush the last char
            while (true) {
                context_data_val = (context_data_val << 1);
                if (context_data_position == bitsPerChar - 1) {
                    context_data.append((char) (context_data_val + offset));
                    break;
                } else
                    context_data_position++;
            }
            context_data.append(' ');
            return context_data.toString();
        }

        public static byte[] decompress(String compressed) {
            if (compressed == null)
                return null;
            if (compressed.isEmpty())
                return new byte[0];
            final int length = compressed.length(), resetValue = 16384, offset = -32;
            ArrayList<String> dictionary = new ArrayList<>();
            int enlargeIn = 4, dictSize = 4, numBits = 3, position = resetValue, index = 1, resb, maxpower, power;
            String entry, w, c;
            StringBuilder sb = new StringBuilder(length >> 1);
            char bits, val = (char) (compressed.charAt(0) + offset);

            for (int i = 0; i < 3; i++) {
                dictionary.add(i, BYTE_STRINGS[i]);
            }

            bits = 0;
            maxpower = 2;
            power = 0;
            while (power != maxpower) {
                resb = val & position;
                position >>= 1;
                if (position == 0) {
                    position = resetValue;
                    val = (char) (compressed.charAt(index++) + offset);
                }
                bits |= (resb > 0 ? 1 : 0) << power++;
            }

            switch (bits) {
                case 0:
                    bits = 0;
                    maxpower = 8;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (compressed.charAt(index++) + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }
                    c = BYTE_STRINGS[bits];
                    break;
                case 1:
                    bits = 0;
                    maxpower = 16;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (compressed.charAt(index++) + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }
                    c = String.valueOf(bits);
                    break;
                default:
                    return new byte[0];
            }
            dictionary.add(c);
            w = c;
            sb.append(w);
            while (true) {
                if (index > length) {
                    return new byte[0];
                }
                int cc = 0;
                maxpower = numBits;
                power = 0;
                while (power != maxpower) {
                    resb = val & position;
                    position >>= 1;
                    if (position == 0) {
                        position = resetValue;
                        val = (char) (compressed.charAt(index++) + offset);
                    }
                    cc |= (resb > 0 ? 1 : 0) << power++;
                }
                switch (cc) {
                    case 0:
                        bits = 0;
                        maxpower = 8;
                        power = 0;
                        while (power != maxpower) {
                            resb = val & position;
                            position >>= 1;
                            if (position == 0) {
                                position = resetValue;
                                val = (char) (compressed.charAt(index++) + offset);
                            }
                            bits |= (resb > 0 ? 1 : 0) << power++;
                        }

                        dictionary.add(BYTE_STRINGS[bits]);
                        cc = dictSize++;
                        enlargeIn--;
                        break;
                    case 1:
                        bits = 0;
                        maxpower = 16;
                        power = 0;
                        while (power != maxpower) {
                            resb = val & position;
                            position >>= 1;
                            if (position == 0) {
                                position = resetValue;
                                val = (char) (compressed.charAt(index++) + offset);
                            }
                            bits |= (resb > 0 ? 1 : 0) << power++;
                        }
                        dictionary.add(String.valueOf(bits));
                        cc = dictSize++;
                        enlargeIn--;
                        break;
                    case 2:
                        try {
                            return sb.toString().getBytes("ISO-8859-1");
                        } catch (UnsupportedEncodingException e) {
                            return null; // should never happen, unless you're deep in the crazy mines.
                        }
                        // this is a possible alternative, but StandardCharsets may add to startup time if loaded early.
//                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                }

                if (enlargeIn == 0) {
                    enlargeIn = 1 << numBits;
                    numBits++;
                }

                if (cc < dictionary.size() && dictionary.get(cc) != null) {
                    entry = dictionary.get(cc);
                } else {
                    if (cc == dictSize) {
                        entry = w + w.charAt(0);
                    } else {
                        return new byte[0];
                    }
                }
                sb.append(entry);

                // Add w+entry[0] to the dictionary.
                dictionary.add(w + entry.charAt(0));
                dictSize++;
                enlargeIn--;

                w = entry;

                if (enlargeIn == 0) {
                    enlargeIn = 1 << numBits;
                    numBits++;
                }

            }

        }

    }
    /**
     * Another temporary class to compare optimizations against the regular ByteStringEncoding.
     */
    public static final class Opt2 {
        private static final String[] BYTE_STRINGS = new String[256];

        static {
            for (int i = 0; i < 256; i++) {
                BYTE_STRINGS[i] = Character.toString((char) i);
            }
        }
        private static final HashMap<String, Integer> contextDictionary = new HashMap<>(256, 0.5f);
        private static final HashSet<String> contextDictionaryToCreate = new HashSet<>(256, 0.5f);
        private static final StringBuilder contextData = new StringBuilder(1024);

        public static String compress(byte[] uncompressed) {
            if (uncompressed == null) return null;
            if (uncompressed.length == 0) return "";
            final int bitsPerChar = 15, offset = 32;
            int i, value;
            String context_c;
            String context_wc;
            String context_w = "";
            int context_enlargeIn = 2; // Compensate for the first entry which should not count
            int context_dictSize = 3;
            int context_numBits = 2;
            int context_data_val = 0;
            int context_data_position = 0;
            int ii;

            contextDictionary.clear();
            contextDictionaryToCreate.clear();
            contextData.setLength(0);

            for (ii = 0; ii < uncompressed.length; ii++) {
                context_c = BYTE_STRINGS[uncompressed[ii] & 255];
                if (!contextDictionary.containsKey(context_c)) {
                    contextDictionary.put(context_c, context_dictSize++);
                    contextDictionaryToCreate.add(context_c);
                }

                context_wc = context_w + context_c;
                if (contextDictionary.containsKey(context_wc)) {
                    context_w = context_wc;
                } else {
                    if (contextDictionaryToCreate.contains(context_w)) {
                        value = (context_w.charAt(0) & 255);
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                contextData.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                        }
                        for (i = 0; i < 8; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                contextData.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value >>= 1;
                        }
                        context_enlargeIn--;
                        if (context_enlargeIn == 0) {
                            context_enlargeIn = 1 << context_numBits++;
                        }
                        contextDictionaryToCreate.remove(context_w);
                    } else {
                        value = contextDictionary.get(context_w);
                        for (i = 0; i < context_numBits; i++) {
                            context_data_val = (context_data_val << 1) | (value & 1);
                            if (context_data_position == bitsPerChar - 1) {
                                context_data_position = 0;
                                contextData.append((char) (context_data_val + offset));
                                context_data_val = 0;
                            } else {
                                context_data_position++;
                            }
                            value >>= 1;
                        }

                    }
                    context_enlargeIn--;
                    if (context_enlargeIn == 0) {
                        context_enlargeIn = 1 << context_numBits++;
                    }
                    // Add wc to the dictionary.
                    contextDictionary.put(context_wc, context_dictSize++);
                    context_w = context_c;
                }
            }

            // Output the code for w.
            if (!context_w.isEmpty()) {
                if (contextDictionaryToCreate.contains(context_w)) {
//                if (context_w.charAt(0) < 256) {
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            contextData.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                    }
                    value = context_w.charAt(0);
                    for (i = 0; i < 8; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            contextData.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }
                    contextDictionaryToCreate.remove(context_w);
                } else {
                    value = contextDictionary.get(context_w);
                    for (i = 0; i < context_numBits; i++) {
                        context_data_val = (context_data_val << 1) | (value & 1);
                        if (context_data_position == bitsPerChar - 1) {
                            context_data_position = 0;
                            contextData.append((char) (context_data_val + offset));
                            context_data_val = 0;
                        } else {
                            context_data_position++;
                        }
                        value >>= 1;
                    }

                }
            }

            // Mark the end of the stream
            value = 2;
            for (i = 0; i < context_numBits; i++) {
                context_data_val = (context_data_val << 1) | (value & 1);
                if (context_data_position == bitsPerChar - 1) {
                    context_data_position = 0;
                    contextData.append((char) (context_data_val + offset));
                    context_data_val = 0;
                } else {
                    context_data_position++;
                }
                value >>= 1;
            }

            // Flush the last char
            while (true) {
                context_data_val = (context_data_val << 1);
                if (context_data_position == bitsPerChar - 1) {
                    contextData.append((char) (context_data_val + offset));
                    break;
                } else
                    context_data_position++;
            }
            contextData.append(' ');
            return contextData.toString();
        }

        public static byte[] decompress(String compressed) {
            if (compressed == null)
                return null;
            if (compressed.isEmpty())
                return new byte[0];
            final int length = compressed.length(), resetValue = 16384, offset = -32;
            ArrayList<String> dictionary = new ArrayList<>();
            int enlargeIn = 4, dictSize = 4, numBits = 3, position = resetValue, index = 1, resb, maxpower, power;
            String entry, w, c;
            StringBuilder sb = new StringBuilder(length >> 1);
            char bits, val = (char) (compressed.charAt(0) + offset);

            for (int i = 0; i < 3; i++) {
                dictionary.add(i, BYTE_STRINGS[i]);
            }

            bits = 0;
            maxpower = 2;
            power = 0;
            while (power != maxpower) {
                resb = val & position;
                position >>= 1;
                if (position == 0) {
                    position = resetValue;
                    val = (char) (compressed.charAt(index++) + offset);
                }
                bits |= (resb > 0 ? 1 : 0) << power++;
            }

            switch (bits) {
                case 0:
                    bits = 0;
                    maxpower = 8;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (compressed.charAt(index++) + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }
                    c = BYTE_STRINGS[bits];
                    break;
                case 1:
                    bits = 0;
                    maxpower = 16;
                    power = 0;
                    while (power != maxpower) {
                        resb = val & position;
                        position >>= 1;
                        if (position == 0) {
                            position = resetValue;
                            val = (char) (compressed.charAt(index++) + offset);
                        }
                        bits |= (resb > 0 ? 1 : 0) << power++;
                    }
                    c = String.valueOf(bits);
                    break;
                default:
                    return new byte[0];
            }
            dictionary.add(c);
            w = c;
            sb.append(w);
            while (true) {
                if (index > length) {
                    return new byte[0];
                }
                int cc = 0;
                maxpower = numBits;
                power = 0;
                while (power != maxpower) {
                    resb = val & position;
                    position >>= 1;
                    if (position == 0) {
                        position = resetValue;
                        val = (char) (compressed.charAt(index++) + offset);
                    }
                    cc |= (resb > 0 ? 1 : 0) << power++;
                }
                switch (cc) {
                    case 0:
                        bits = 0;
                        maxpower = 8;
                        power = 0;
                        while (power != maxpower) {
                            resb = val & position;
                            position >>= 1;
                            if (position == 0) {
                                position = resetValue;
                                val = (char) (compressed.charAt(index++) + offset);
                            }
                            bits |= (resb > 0 ? 1 : 0) << power++;
                        }

                        dictionary.add(BYTE_STRINGS[bits]);
                        cc = dictSize++;
                        enlargeIn--;
                        break;
                    case 1:
                        bits = 0;
                        maxpower = 16;
                        power = 0;
                        while (power != maxpower) {
                            resb = val & position;
                            position >>= 1;
                            if (position == 0) {
                                position = resetValue;
                                val = (char) (compressed.charAt(index++) + offset);
                            }
                            bits |= (resb > 0 ? 1 : 0) << power++;
                        }
                        dictionary.add(String.valueOf(bits));
                        cc = dictSize++;
                        enlargeIn--;
                        break;
                    case 2:
                        try {
                            return sb.toString().getBytes("ISO-8859-1");
                        } catch (UnsupportedEncodingException e) {
                            return null; // should never happen, unless you're deep in the crazy mines.
                        }
                        // this is a possible alternative, but StandardCharsets may add to startup time if loaded early.
//                    return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
                }

                if (enlargeIn == 0) {
                    enlargeIn = 1 << numBits;
                    numBits++;
                }

                if (cc < dictionary.size() && dictionary.get(cc) != null) {
                    entry = dictionary.get(cc);
                } else {
                    if (cc == dictSize) {
                        entry = w + w.charAt(0);
                    } else {
                        return new byte[0];
                    }
                }
                sb.append(entry);

                // Add w+entry[0] to the dictionary.
                dictionary.add(w + entry.charAt(0));
                dictSize++;
                enlargeIn--;

                w = entry;

                if (enlargeIn == 0) {
                    enlargeIn = 1 << numBits;
                    numBits++;
                }

            }

        }

    }
}
