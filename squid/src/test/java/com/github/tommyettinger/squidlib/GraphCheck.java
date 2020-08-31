package com.github.tommyettinger.squidlib;

import space.earlygrey.simplegraphs.DirectedGraph;
import space.earlygrey.simplegraphs.UndirectedGraph;
import squidpony.squidai.CustomDijkstraMap;
import squidpony.squidai.DijkstraMap;
import squidpony.squidgrid.Adjacency;
import squidpony.squidgrid.Direction;
import squidpony.squidgrid.mapping.DungeonGenerator;
import squidpony.squidgrid.mapping.DungeonUtility;
import squidpony.squidmath.AStarSearch;
import squidpony.squidmath.Coord;
import squidpony.squidmath.GreasedRegion;
import squidpony.squidmath.StatefulRNG;

import java.util.ArrayList;
import java.util.Collections;

import static squidpony.squidgrid.Measurement.CHEBYSHEV;

public class GraphCheck {
    public int DIMENSION = 28;
    public DungeonGenerator dungeonGen = new DungeonGenerator(DIMENSION, DIMENSION, new StatefulRNG(0x1337BEEFDEAL));
    public char[][] map;
    public double[][] astarMap;
    public GreasedRegion floors;
    public int floorCount;
    public Coord[] floorArray;
    public Coord[][] nearbyMap;
    public int[] customNearbyMap;
    public Adjacency adj;
    public DijkstraMap dijkstra;
    public CustomDijkstraMap customDijkstra;
    public StatefulRNG srng;
    public AStarSearch as;
    public ArrayList<Coord> path;

    public DirectedGraph<Coord> simpleDirectedGraph;
    public UndirectedGraph<Coord> simpleUndirectedGraph;

    public space.earlygrey.simplegraphs.utils.Heuristic<Coord> simpleHeu;

    public squidpony.squidai.graph.DirectedGraph<Coord> squidDirectedGraph;
    public squidpony.squidai.graph.UndirectedGraph<Coord> squidUndirectedGraph;
    public squidpony.squidai.graph.DefaultGraph squidDefaultGraph;
    public squidpony.squidai.graph.CostlyGraph squidCostlyGraph;

    public GraphCheck(){
        Coord.expandPoolTo(DIMENSION, DIMENSION);
        map = dungeonGen.generate();
        floors = new GreasedRegion(map, '.');
        floorCount = floors.size();
        floorArray = floors.asCoords();
        System.out.println("Floors: " + floorCount);
        System.out.println("Percentage walkable: " + floorCount * 100.0 / (DIMENSION * DIMENSION) + "%");
        astarMap = DungeonUtility.generateAStarCostMap(map, Collections.<Character, Double>emptyMap(), 1);
        as = new AStarSearch(astarMap, AStarSearch.SearchType.CHEBYSHEV);
        nearbyMap = new Coord[DIMENSION][DIMENSION];
        customNearbyMap = new int[DIMENSION * DIMENSION];
        GreasedRegion tmp = new GreasedRegion(DIMENSION, DIMENSION);
        adj = new Adjacency.BasicAdjacency(DIMENSION, DIMENSION, CHEBYSHEV);
        adj.blockingRule = 0;
        srng = new StatefulRNG(0x1337BEEF1337CA77L);
        Coord c;
        for (int i = 1; i < DIMENSION - 1; i++) {
            for (int j = 1; j < DIMENSION - 1; j++) {
                if(map[i][j] == '#')
                    continue;
                c = tmp.empty().insert(i, j).flood(floors, 8).remove(i, j).singleRandom(srng);
                nearbyMap[i][j] = c;
                customNearbyMap[adj.composite(i, j, 0, 0)] = adj.composite(c.x, c.y, 0, 0);
            }
        }
        dijkstra = new DijkstraMap(map, CHEBYSHEV, new StatefulRNG(0x1337BEEF));
        dijkstra.setBlockingRequirement(0);
        customDijkstra = new CustomDijkstraMap(map, adj, new StatefulRNG(0x1337BEEF));
        path = new ArrayList<>(DIMENSION << 2);

        simpleDirectedGraph = new DirectedGraph<>(floors);
        simpleUndirectedGraph = new UndirectedGraph<>(floors);

        // should print true
        System.out.println(floors.contains(Coord.get(22, 21)));
        // should print true, but does not due to get() not seeing the Coord in the NodeMap
        System.out.println(simpleDirectedGraph.getVertices().contains(Coord.get(22, 21)));

        squidDirectedGraph   = new squidpony.squidai.graph.DirectedGraph<>(floors);
        squidUndirectedGraph = new squidpony.squidai.graph.UndirectedGraph<>(floors);
        squidDefaultGraph = new squidpony.squidai.graph.DefaultGraph(map, true);
        squidCostlyGraph = new squidpony.squidai.graph.CostlyGraph(astarMap, true);
        simpleHeu = new space.earlygrey.simplegraphs.utils.Heuristic<Coord>() {
            @Override
            public float getEstimate(Coord currentNode, Coord targetNode) {
                return Math.max(Math.abs(currentNode.x - targetNode.x), Math.abs(currentNode.y - targetNode.y));
            }
        };
        Coord center;
        Direction[] outer = Direction.CLOCKWISE;
        Direction dir;
        for (int i = floorCount - 1; i >= 0; i--) {
            center = floorArray[i];
            for (int j = 0; j < 8; j++) {
                dir = outer[j];
                if(floors.contains(center.x + dir.deltaX, center.y + dir.deltaY))
                {
                    simpleDirectedGraph.addEdge(center, center.translate(dir));
                    squidDirectedGraph.addEdge(center, center.translate(dir));
                    if(!simpleUndirectedGraph.edgeExists(center, center.translate(dir)))
                    {
                        simpleUndirectedGraph.addEdge(center, center.translate(dir));
                        squidUndirectedGraph.addEdge(center, center.translate(dir));
                    }
                }
            }
        }
    }

    public static void main(String[] args){
        GraphCheck gc = new GraphCheck();
        System.out.println(gc.floorCount);
        System.out.println(gc.simpleDirectedGraph.getVertices().size());
    }
}
