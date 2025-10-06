package squid.squad;

import com.github.yellowstonegames.grid.Coord;

import java.util.*;

public class BitCoordSet extends AbstractCollection<Coord> {
    public BitSet bits;
    public int width;
    public int height;
    protected transient CoordIterator iterator1;
    protected transient CoordIterator iterator2;

    public BitCoordSet(int width, int height){
        this.width = width;
        this.height = height;
        this.bits = new BitSet(width * height);
    }

    @Override
    public Iterator<Coord> iterator() {
        if (iterator1 == null || iterator2 == null) {
            iterator1 = new CoordIterator(this);
            iterator2 = new CoordIterator(this);
        }
        if (!iterator1.valid) {
            iterator1.reset();
            iterator1.valid = true;
            iterator2.valid = false;
            return iterator1;
        }
        iterator2.reset();
        iterator2.valid = true;
        iterator1.valid = false;
        return iterator2;
    }

    @Override
    public int size() {
        return bits.cardinality();
    }

    @Override
    public boolean contains(Object o) {
        Coord c = (Coord) o;
        if(c.x < 0 || c.y < 0 || c.x >= width || c.y >= height) return false;
        return bits.get(c.y * width + c.x);
    }

    @Override
    public boolean add(Coord coord) {
        if(coord.x < 0 || coord.y < 0 || coord.x >= width || coord.y >= height) return false;
        final int i = coord.y * width + coord.x;
        final boolean r = !bits.get(i);
        bits.set(i);
        return r;
    }

    @Override
    public boolean remove(Object o) {
        Coord coord = (Coord) o;
        if(coord.x < 0 || coord.y < 0 || coord.x >= width || coord.y >= height) return false;
        final int i = coord.y * width + coord.x;
        final boolean r = bits.get(i);
        bits.clear(i);
        return r;
    }

    public static class CoordIterator implements Iterator<Coord> {
        BitCoordSet parent;
        public int index = 0;
        boolean valid = true;

        public CoordIterator(BitCoordSet parent){
            this.parent = parent;
            index = parent.bits.nextSetBit(0);
        }
        @Override
        public boolean hasNext() {
            if (!valid) {
                throw new RuntimeException("#iterator() cannot be used nested.");
            }
            return index != -1;
        }

        @Override
        public Coord next() {
            if (index >= parent.bits.size() || index < 0)
                throw new NoSuchElementException(String.valueOf(index));
            if (!valid) {
                throw new RuntimeException("#iterator() cannot be used nested.");
            }

            Coord c = Coord.get(index / parent.width, index % parent.width);
            index = parent.bits.nextSetBit(index+1);
            return c;
        }

        @Override
        public void remove() {
            parent.bits.clear(parent.bits.previousSetBit(index-1));
        }
        public void reset() {
            index = parent.bits.nextSetBit(0);
        }
    }
}
