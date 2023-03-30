/*
 * Copyright (c) 2022 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.tommyettinger.ds;

import squidpony.squidmath.Coord;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A variant on jdkgdxds' {@link ObjectSet} class that only holds Coord items, and can do so more efficiently.
 * This assumes all Coord keys are in the Coord pool; that is, {@link Coord#expandPoolTo(int, int)} has been called with
 * the maximum values for Coord x and y.
 */
public class CantorCoordSet extends ObjectSet<Coord> {
    public CantorCoordSet() {
        super();
    }

    public CantorCoordSet(int initialCapacity) {
        super(initialCapacity);
    }

    public CantorCoordSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public CantorCoordSet(ObjectSet<? extends Coord> set) {
        super(set);
    }

    public CantorCoordSet(Collection<? extends Coord> coll) {
        super(coll);
    }

    public CantorCoordSet(Coord[] array, int offset, int length) {
        super(array, offset, length);
    }

    public CantorCoordSet(Coord[] array) {
        super(array);
    }

    @Override
    protected int place(Object item) {
        final int x = ((Coord)item).x, y = ((Coord)item).y;
        return y + ((x + y + 6) * (x + y + 7) >>> 1) & mask;
    }

    @Override
    protected boolean equate(Object left, @Nullable Object right) {
        return left == right;
    }

    public static CantorCoordSet with(Coord item) {
        CantorCoordSet set = new CantorCoordSet(1);
        set.add(item);
        return set;
    }

    public static CantorCoordSet with (Coord... array) {
        return new CantorCoordSet(array);
    }
}
