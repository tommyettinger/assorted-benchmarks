/*
 * Copyright (c) 2022-2023 See AUTHORS file.
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
package com.github.yellowstonegames.path;

import com.github.tommyettinger.digital.BitConversion;
import com.github.tommyettinger.ds.BinaryHeap;
//import com.badlogic.gdx.utils.BinaryHeap;
import com.github.tommyettinger.ds.ObjectDeque;
import com.github.tommyettinger.ds.ObjectList;
import com.github.tommyettinger.ds.ObjectOrderedSet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

class AlgorithmImplementations<V> {

    //================================================================================
    // Fields
    //================================================================================

    private final Graph<V> graph;
    private final BinaryHeap<Node<V>> heap;
    private final ObjectDeque<Node<V>> queue;
    private int runID;

    //================================================================================
    // Constructor
    //================================================================================

    AlgorithmImplementations(Graph<V> graph) {
        this.graph = graph;
        heap = new BinaryHeap<>();
        queue = new ObjectDeque<>();
    }

    //================================================================================
    // Util
    //================================================================================

    private void init() {
        runID++;
    }

    int getRunID() {
        return runID;
    }

    //================================================================================
    // Connectivity
    //================================================================================

    boolean isReachable(Node<V> start, Node<V> target) {
        return !findShortestPath(start, target).isEmpty();
    }

    //================================================================================
    // Searches
    //================================================================================

    void breadthFirstSearch(Node<V> vertex, Graph<V> tree, int maxVertices, int maxDepth) {
        if (maxDepth <= 0 ) return;
        init();

        vertex.resetAlgorithmAttributes(runID);
        vertex.visited = true;
        ObjectDeque<Node<V>> queue = this.queue;
        queue.clear();
        queue.addLast(vertex);

        while(!queue.isEmpty()) {
            Node<V> v = queue.pollFirst();
            tree.addVertex(v.object);
            if (v.prev != null) tree.addEdge(v.object, v.prev.object);
            if (v.i == maxDepth) continue;
            if (tree.size() == maxVertices) break;
            int n = v.outEdges.size();
            for (int i = 0; i < n; i++) {
                Connection<V> e = v.outEdges.get(i);
                Node<V> w = e.b;
                w.resetAlgorithmAttributes(runID);
                if (!w.visited) {
                    w.visited = true;
                    w.i = v.i+1;
                    w.prev = v;
                    queue.addLast(w);
                }
            }
        }
    }

    void depthFirstSearch(Node<V> vertex, Graph<V> tree, int maxVertices, int maxDepth) {
        init();

        vertex.resetAlgorithmAttributes(runID);
        ObjectDeque<Node<V>> queue = this.queue;
        queue.clear();
        queue.addLast(vertex);

        while(!queue.isEmpty()) {
            Node<V> v = queue.pollFirst();
            if (!v.visited) {
                tree.addVertex(v.object);
                if (v.prev != null) tree.addEdge(v.object, v.prev.object);
                if (v.i == maxDepth) continue;
                if (tree.size() == maxVertices) break;
                v.visited = true;
                int n = v.outEdges.size();
                for (int i = 0; i < n; i++) {
                    Connection<V> e = v.outEdges.get(i);
                    Node<V> w = e.b;
                    w.resetAlgorithmAttributes(runID);
                    w.i = v.i+1;
                    w.prev = v;
                    queue.addFirst(w);
                }
            }
        }
    }

    //================================================================================
    // Shortest Paths
    //================================================================================

    float findMinimumDistance(Node<V> start, Node<V> target) {
        Node<V> end = aStarSearch(start, target, null);
        if (end==null) return Float.MAX_VALUE;
        else return end.distance;
    }

    ObjectDeque<V> findShortestPath(Node<V> start, Node<V> target) {
        ObjectDeque<V> path = new ObjectDeque<>();
        findShortestPath(start, target, path);
        return path;
    }

    boolean findShortestPath(Node<V> start, Node<V> target, ObjectDeque<V> path) {
        return findShortestPath(start, target, path, null);
    }

    ObjectDeque<V> findShortestPath(Node<V> start, Node<V> target, Heuristic<V> heuristic) {
        ObjectDeque<V> path = new ObjectDeque<>();
        findShortestPath(start, target, path, heuristic);
        return path;
    }

    boolean findShortestPath(Node<V> start, Node<V> target, ObjectDeque<V> path, Heuristic<V> heuristic) {
        Node<V> end = aStarSearch(start, target, heuristic);
        if (end==null) {
            return false;
        }
        Node<V> v = end;
        while(v.prev!=null) {
            path.addFirst(v.object);
            v = v.prev;
        }
        path.addFirst(start.object);
        return true;
    }

    private Node<V> aStarSearch(Node<V> start, Node<V> target, Heuristic<V> heuristic) {
        init();

        boolean hasHeuristic = heuristic != null;
        
        start.resetAlgorithmAttributes(runID);
        start.distance = 0;

        heap.add(start);

        while(heap.size != 0) {
            Node<V> u = heap.pop();
            if (u == target) {
                heap.clear();
                return u;
            }
            if (!u.visited) {
                u.visited = true;
                int n = u.outEdges.size();
                for (int i = 0; i < n; i++) {
                    Connection<V> e = u.outEdges.get(i);
                    Node<V> v = e.b;
                    v.resetAlgorithmAttributes(runID);
                    if (!v.visited) {
                        float newDistance = u.distance + e.weight;
                        if (newDistance < v.distance) {
                            v.distance = newDistance;
                            v.prev = u;
                            if (hasHeuristic && !v.seen) {
                                v.estimate = heuristic.estimate(v.object, target.object);
                            }
                            if (!v.seen) {
                                heap.add(v, v.distance + v.estimate);
                            } else {
                                heap.setValue(v, v.distance + v.estimate);
                            }
                            v.seen = true;
                        }
                    }
                }

//                ObjectList<Connection<V>> outEdges = u.outEdges;
//                for (Connection<V> e : outEdges) {
//                    Node<V> v = e.getNodeB();
//                    v.resetAlgorithmAttributes(runID);
//                    if (!v.visited) {
//                        float newDistance = u.distance + e.getWeight();
//                        if (newDistance < v.distance) {
//                            v.distance = newDistance;
//                            v.prev = u;
////                            v.connection = e;
//                            if (heuristic != null && !v.seen) {
//                                v.estimate = heuristic.estimate(v.getObject(), target.getObject());
//                            }
//                            if (!v.seen) {
//                                heap.add(v, v.distance + v.estimate);
//                            } else {
//                                heap.setValue(v, v.distance + v.estimate);
//                            }
//                            v.index = (u.index + 1);
//                            v.seen = true;
//                        }
//                    }
//                }
            }
        }
        heap.clear();
        return null;
    }

    //================================================================================
    // Topological sorting
    //================================================================================

    boolean topologicalSort(ObjectList<V> sortedVertices) {
        sortedVertices.clear();
        init();
        ObjectOrderedSet<Node<V>> set = new ObjectOrderedSet<>(graph.vertexMap.values());
        boolean success = true;
        while (success && !set.isEmpty()) {
            success = recursiveTopologicalSort(sortedVertices, set.first(), set);
        }
        if (success) {
            sortedVertices.reverse();
        }

        return success;
    }

    boolean topologicalSort() {
        ObjectList<V> sortedVertices = new ObjectList<>();
        init();
        ObjectOrderedSet<Node<V>> set = new ObjectOrderedSet<>(graph.vertexMap.values());
        boolean success = true;
        while (success && !set.isEmpty()) {
            success = recursiveTopologicalSort(sortedVertices, set.first(), set);
        }
        if (success) {
            for (int i = sortedVertices.size()-1; i >= 0; i--) {
                graph.vertexMap.order().swap(graph.vertexMap.size() - 1, graph.vertexMap.order().indexOf(sortedVertices.get(i)));
            }
        }
        return success;
    }

    private boolean recursiveTopologicalSort(ObjectList<V> sortedVertices, Node<V> v, Set<Node<V>> set) {
        v.resetAlgorithmAttributes(runID);

        if (v.visited) return true;
        if (v.seen) {
            // not a DAG
            return false;
        }
        v.seen = true;
        int n = v.outEdges.size();
        for (int i = 0; i < n; i++) {
            Connection<V> e = v.outEdges.get(i);
            boolean success = recursiveTopologicalSort(sortedVertices, e.b, set);
            if (!success) return false;
        }
        v.seen = false;
        v.visited = true;
        sortedVertices.add(v.object);
        set.remove(v);
        return true;
    }

    //================================================================================
    // Minimum spanning trees
    //================================================================================

    final Comparator<Connection<V>> weightComparator = (o1, o2) -> BitConversion.floatToRawIntBits(o1.weight - o2.weight);

    final Comparator<Connection<V>> reverseWeightComparator = (o1, o2) -> BitConversion.floatToRawIntBits(o2.weight - o1.weight);
    
    // adapted from https://www.baeldung.com/java-spanning-trees-kruskal

    Graph<V> kruskalsMinimumWeightSpanningTree(boolean minSpanningTree) {
        init();

        Graph<V> spanningTree = graph.createNew();

        spanningTree.addVertices(graph.vertexMap.keySet());

        ObjectList<Connection<V>> edgeList = new ObjectList<>(graph.edgeMap.values());

        edgeList.sort(minSpanningTree ? weightComparator : reverseWeightComparator);

        int maxNodes = graph.size() - 1;
        final int totalEdges = edgeList.size();
        int edgeCount = 0;

        for (int i = 0; i < totalEdges; i++) {
            final Connection<V> edge = edgeList.get(i);
            if (doesEdgeCreateCycle(edge.a, edge.b)) {
                continue;
            }
            spanningTree.addConnection(edge.a, edge.b, edge.weight);
            edgeCount++;
            if (edgeCount == maxNodes) {
                break;
            }
        }

        return spanningTree;
    }

    private void unionByRank(Node<V> rootU, Node<V> rootV) {
        if (rootU.i < rootV.i) {
            rootU.prev = rootV;
        } else {
            rootV.prev = rootU;
            if (rootU.i == rootV.i) rootU.i++;
        }
    }

    private Node<V> find(Node<V> node) {
        if (node.prev.equals(node)) {
            return node;
        } else {
            return find(node.prev);
        }
    }
    private Node<V> pathCompressionFind(Node<V> node) {
        if (node.prev.equals(node)) {
            return node;
        } else {
            Node<V> parentNode = find(node.prev);
            node.prev = parentNode;
            return parentNode;
        }
    }

    private boolean doesEdgeCreateCycle(Node<V> u, Node<V> v) {
        if (u.resetAlgorithmAttributes(runID)) u.prev = u;
        if (v.resetAlgorithmAttributes(runID)) v.prev = v;
        Node<V> rootU = pathCompressionFind(u);
        Node<V> rootV = pathCompressionFind(v);
        if (rootU.equals(rootV)) {
            return true;
        }
        unionByRank(rootU, rootV);
        return false;
    }

    //================================================================================
    // Cycle detection
    //================================================================================

    boolean containsCycle(Graph<V> graph) {
        if (graph.size() < 3 || graph.getEdgeCount() < 3) return false;
        init();
        HashSet<Node<V>> set = new HashSet<>();
        for (Node<V> v : graph.getNodes()) {
            v.resetAlgorithmAttributes(runID);
            if (detectCycleDFS(v, null, set)) {
                init();
                return true;
            }
            set.clear();
        }
        return false;
    }

    private boolean detectCycleDFS(Node<V> v, Node<V> parent, Set<Node<V>> recursiveStack) {
        v.visited = true;
        recursiveStack.add(v);
        int n = v.outEdges.size();
        for (int i = 0; i < n; i++) {
            Connection<V> e = v.outEdges.get(i);
            Node<V> u = e.b;
            if (!graph.isDirected() && u.equals(parent)) continue;
            u.resetAlgorithmAttributes(runID);
            if (recursiveStack.contains(u)) {
                return true;
            }
            if (!u.visited) {
                if (detectCycleDFS(u, v, recursiveStack)) return true;
            }
        }
        recursiveStack.remove(v);
        return false;
    }
}

