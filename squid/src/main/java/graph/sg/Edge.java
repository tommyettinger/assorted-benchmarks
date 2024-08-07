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

public abstract class Edge<V> {


    Edge(){}
    
    public abstract V getA();
    public abstract V getB();
    public abstract boolean hasEndpoints(V u, V v);
    public boolean hasEndpoint(V u) {
        return getA().equals(u) || getB().equals(u);
    }

    public abstract float getWeight();
    public abstract void setWeight(float weight);
    public abstract void setWeight(WeightFunction<V> weightFunction);
    abstract WeightFunction<V> getWeightFunction();

    abstract Node<V> getInternalNodeA();
    abstract Node<V> getInternalNodeB();

    //abstract void set(Node<V> a, Node<V> b);
    abstract void set(Node<V> a, Node<V> b, WeightFunction<V> weightFunction);
}
