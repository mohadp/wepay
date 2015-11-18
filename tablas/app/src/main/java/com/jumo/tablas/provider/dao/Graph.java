package com.jumo.tablas.provider.dao;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Moha on 11/14/15.
 */
public class Graph<MyType> {
    private LinkedHashMap<MyType, LinkedHashSet<MyType>> mGraph;

    public Graph(LinkedHashSet<MyType> nodes){
        mGraph = new LinkedHashMap<MyType, LinkedHashSet<MyType>>();
        for(MyType node : nodes){
            mGraph.put(node, new LinkedHashSet<MyType>());
        }
    }

    public Graph(){
        mGraph = new LinkedHashMap<MyType, LinkedHashSet<MyType>>();
    }

    /**
     * Adds a path to the graph. Since the path is undirected, node2 is added to node1's
     * connections and node1 is added to node2's connections
     * @param node1
     * @param node2
     */
    public void addUndirectedPath(MyType node1, MyType node2){
        addDirectedPath(node1, node2);
        addDirectedPath(node2, node1);
    }

    public void addDirectedPath(MyType node1, MyType node2){
        LinkedHashSet<MyType> pathTo = mGraph.get(node1);
        if(pathTo == null){
            pathTo = new LinkedHashSet<MyType>();
            mGraph.put(node1, pathTo);
        }
        pathTo.add(node2); //adding a preexistent node will keep only one (since this is a Set, a LinkedHashSet).
    }

    /**
     * Adds an independent node, without any connections
     * @param node
     */
    public void addNode(MyType node){
        if(mGraph.get(node) == null) {
            mGraph.put(node, new LinkedHashSet<MyType>());
        }
    }

    public LinkedHashSet<MyType> getNeighbors(MyType node){
        return mGraph.get(node);
    }

    public Set<MyType> getNodes(){
        return mGraph.keySet();
    }
}
