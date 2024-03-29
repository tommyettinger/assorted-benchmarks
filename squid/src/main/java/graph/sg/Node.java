/*
MIT License

Copyright (c) 2020 earlygrey

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package graph.sg;

import com.github.tommyettinger.ds.ObjectDeque;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Node<V> extends com.github.tommyettinger.ds.BinaryHeap.Node {

    //================================================================================
    // Graph structure related members
    //================================================================================

    final int idHash;
    final V object;

    Map<Node<V>, Connection<V>> neighbours = new LinkedHashMap<>();
    private ObjectDeque<Connection<V>> outEdges = new ObjectDeque<>();
    private ObjectDeque<Connection<V>> inEdges;

    //================================================================================
    // Node map fields
    //================================================================================

    final int objectHash;
    int mapHash;
    Node<V> nextInOrder = null, prevInOrder = null;
    Node<V> nextInBucket = null;

    //================================================================================
    // Constructor
    //================================================================================

    Node(V v, boolean trackInEdges, int objectHash) {
        super(0f);
        this.object = v;
        this.objectHash = objectHash;
        idHash = System.identityHashCode(this);
        if (trackInEdges) setInEdges(new ObjectDeque<>());
    }

    Node(V v, boolean trackInEdges, int objectHash, float heapValue) {
        super(heapValue);
        this.object = v;
        this.objectHash = objectHash;
        idHash = System.identityHashCode(this);
        if (trackInEdges) setInEdges(new ObjectDeque<>());
    }

    //================================================================================
    // Internal methods
    //================================================================================

    Connection<V> getEdge(Node<V> v) {
        return neighbours.get(v);
    }

    void addEdge(Connection<V> edge) {
        Node<V> to = edge.getNodeB();
        neighbours.put(to, edge);
        getOutEdges().add(edge);
        if (to.getInEdges() != null) to.getInEdges().add(edge);
    }

    Connection<V> removeEdge(Node<V> v) {
        Connection<V> edge = neighbours.remove(v);
        if (edge == null) return null;
        getOutEdges().remove(edge);
        if (v.getInEdges() != null) v.getInEdges().remove(edge);
        return edge;
    }

    void disconnect() {
        neighbours.clear();
        getOutEdges().clear();
        if (getInEdges() != null) getInEdges().clear();
    }

    //================================================================================
    // Public Methods
    //================================================================================

    public Collection<Connection<V>> getConnections() {
        return getOutEdges();
    }

    public V getObject() {
        return object;
    }

    public int getInDegree() {
        return getInEdges() == null ? getOutDegree() : getInEdges().size();
    }

    public int getOutDegree() {
        return getOutEdges().size();
    }

    //================================================================================
    // Algorithm fields and methods
    //================================================================================

    // util fields for algorithms, don't store data in them
    private boolean processed;
    private boolean seen;
    private float distance;
    private float estimate;
    private Node<V> prev;
    private Connection<V> connection;
    private int index;
    private int lastRunID = -1;

    public boolean resetAlgorithmAttribs(int runID) {
        if (runID == lastRunID) return false;
        setProcessed(false);
        setPrev(null);
        setConnection(null);
        setDistance(Float.MAX_VALUE);
        setEstimate(0);
        setIndex(0);
        setSeen(false);
        lastRunID = runID;
        return true;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getEstimate() {
        return estimate;
    }

    public void setEstimate(float estimate) {
        this.estimate = estimate;
    }

    public Node<V> getPrev() {
        return prev;
    }

    public void setPrev(Node<V> prev) {
        this.prev = prev;
    }

    public Connection<V> getConnection() {
        return connection;
    }

    public void setConnection(Connection<V> connection) {
        this.connection = connection;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLastRunID() {
        return lastRunID;
    }

    public void setLastRunID(int lastRunID) {
        this.lastRunID = lastRunID;
    }

    //================================================================================
    // Misc
    //================================================================================

    // equals() compares by reference equality

    @Override
    public int hashCode() {
        return idHash;
    }

    @Override
    public String toString() {
        return "["+object+"]";
    }

    public ObjectDeque<Connection<V>> getOutEdges() {
        return outEdges;
    }

    public void setOutEdges(ObjectDeque<Connection<V>> outEdges) {
        this.outEdges = outEdges;
    }

    public ObjectDeque<Connection<V>> getInEdges() {
        return inEdges;
    }

    public void setInEdges(ObjectDeque<Connection<V>> inEdges) {
        this.inEdges = inEdges;
    }
}
