package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.random.PouchRandom;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.Direction;
import com.github.yellowstonegames.grid.Region;
import com.github.yellowstonegames.path.Algorithms;
import com.github.yellowstonegames.path.Heuristic;
import com.github.yellowstonegames.path.UndirectedGraph;
import com.github.yellowstonegames.place.DungeonProcessor;

import java.util.Arrays;

/**
 * This is for experimenting with changes to SquidSquad's pathfinding code.
 * Currently, it seems very slightly slower using libGDX's BinaryHeap instead
 * of jdkgdxds' BinaryHeap.
 */
public class Main extends ApplicationAdapter {

    public static final int WIDTH = 60;
    public static final int HEIGHT = 60;

    public final PouchRandom random = new PouchRandom(1234567890L);

    public DungeonProcessor dungeonGen = new DungeonProcessor(WIDTH, HEIGHT, random);
    public char[][] map;

    public Region floors;
    public int floorCount;
    public Coord[] floorArray;

    public ObjectDeque<Coord> path;
    public UndirectedGraph<Coord> undirectedGraph;

    @Override
    public void create() {
        com.github.yellowstonegames.grid.Coord.expandPoolTo(WIDTH, HEIGHT);
        map = dungeonGen.generate();
        System.out.println();
        com.github.yellowstonegames.place.DungeonTools.debugPrint(map);
        System.out.println();
        floors = new Region(map, '.');
        floorCount = floors.size();
        floorArray = floors.asCoords();
        path = new ObjectDeque<>(WIDTH + HEIGHT << 1);
        undirectedGraph = new UndirectedGraph<>(Arrays.asList(floorArray));

        Coord center;
        Direction[] outer = Direction.CLOCKWISE;
        Direction dir;
        for (int i = floorCount - 1; i >= 0; i--) {
            center = floorArray[i];
            for (int j = 0; j < 8; j++) {
                dir = outer[j];
                if (floors.contains(center.x + dir.deltaX, center.y + dir.deltaY)) {
                    if (!undirectedGraph.edgeExists(center, center.translate(dir))) {
                        undirectedGraph.addEdge(center, center.translate(dir));
                    }
                }
            }
        }

        final Algorithms<Coord> algo = undirectedGraph.algorithms();

        long previousTime = System.nanoTime(), startTime = previousTime;
        for (int i = 0; ; i++) {
            Coord start = random.randomElement(floorArray);
            Coord end = random.randomElement(floorArray);
            path.clear();
            algo.findShortestPath(start, end, path, Heuristic.CHEBYSHEV);

            double diff = (double)(-previousTime + (previousTime = System.nanoTime()));
            if((i & i - 1) == 0)
                System.out.printf("Path #%d with length %d took %g ns; total time %g ns\n", i, path.size,
                        diff, (double)(previousTime - startTime));
        }
    }
}