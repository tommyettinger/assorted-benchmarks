package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.github.tommyettinger.random.PouchRandom;
import com.github.yellowstonegames.grid.Coord;
import com.github.yellowstonegames.grid.Direction;
import com.github.yellowstonegames.grid.Region;
import com.github.yellowstonegames.place.DungeonProcessor;
import space.earlygrey.simplegraphs.Path;
import space.earlygrey.simplegraphs.UndirectedGraph;
import space.earlygrey.simplegraphs.algorithms.SearchStep;
import space.earlygrey.simplegraphs.algorithms.UndirectedGraphAlgorithms;

import java.util.Arrays;

/**
 * OK. Some weird profiling results...
 * According to VisualVM, Connection.getWeight() alone takes 22.8% of the app's total time.
 * Connection.getA() and getB() each take 3.3% of the total time.
 * I'm not sure why these methods in particular take so much time, but they are called very often.
 * One of the few other high times would be the self time of AStarSearch.update(), which takes 34.6% .
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

    public Path<Coord> simplePath;
    public UndirectedGraph<Coord> simpleUndirectedGraph;
    public space.earlygrey.simplegraphs.utils.Heuristic<Coord> simpleHeu;

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
        simplePath = new Path<>(WIDTH + HEIGHT << 1, false);
        simpleUndirectedGraph = new UndirectedGraph<>(Arrays.asList(floorArray));
        simpleHeu = (currentNode, targetNode) ->
                Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));

        Coord center;
        Direction[] outer = Direction.CLOCKWISE;
        Direction dir;
        for (int i = floorCount - 1; i >= 0; i--) {
            center = floorArray[i];
            for (int j = 0; j < 8; j++) {
                dir = outer[j];
                if (floors.contains(center.x + dir.deltaX, center.y + dir.deltaY)) {
                    if (!simpleUndirectedGraph.edgeExists(center, center.translate(dir))) {
                        simpleUndirectedGraph.addEdge(center, center.translate(dir));
                    }
                }
            }
        }

        final UndirectedGraphAlgorithms<Coord> algo = simpleUndirectedGraph.algorithms();

        long previousTime = System.nanoTime(), startTime = previousTime;
        for (int i = 0; ; i++) {
            Coord start = random.randomElement(floorArray);
            Coord end = random.randomElement(floorArray);
            simplePath.clear();
            simplePath.addAll(algo.findShortestPath(start, end, simpleHeu, SearchStep::vertex));

            double diff = (double)(-previousTime + (previousTime = System.nanoTime()));
            if((i & i - 1) == 0)
                System.out.printf("Path #%d with length %d took %g ns; total time %g ns\n", i, simplePath.size,
                        diff, (double)(previousTime - startTime));
        }
    }
}