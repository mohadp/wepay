package com.jumo.tablas.provider.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by Moha on 11/12/15.
 */
public class GraphDFS<MyType> {

    private Graph<MyType> mGraph;
    private LinkedHashMap<MyType, Boolean> mNodeVisited;
    private GraphPaths mPathsVisited;
    private OnGraphTraverse<MyType> mOnGraphTraverse;


    public GraphDFS(Graph<MyType> graph){
        mGraph = graph;
        mNodeVisited = new LinkedHashMap<MyType, Boolean>();
        mPathsVisited = new GraphPaths();
    }

    public void resetState(){
        mNodeVisited.clear();
        mPathsVisited.clear();
    }

    public void visitAllPaths(){
        for(MyType node : mGraph.getNodes()){
            dfsEdges(node);
        }
    }

    public void dfsEdges(MyType node){
        if(mNodeVisited.get(node) != null && mNodeVisited.get(node) == true){
            return; //if node is visited, then we do not not need to re-visit all its neighbors since we have done that already.
        }else{
            //We are visiting an unvisited node, so we can mark it as "visited"; we can also call the OnNewNodeVisited method.
            mNodeVisited.put(node, true);
            mOnGraphTraverse.onDfsNewNodeVisited(node);
        }

        LinkedHashSet<MyType> connectedNodes = mGraph.getNeighbors(node);
        if(connectedNodes.size() == 0){
            //We want to notify whenever there is a node that is disconnected from anything (there are no edges to/from it).
            mOnGraphTraverse.onDfsUnconnectedNodeFound(node);
        }

        for(MyType vertex : connectedNodes){
            if(!mPathsVisited.containsPath(node, vertex)) {
                mPathsVisited.addPath(node, vertex);
                mOnGraphTraverse.onDfsNewEdgeTraveled(node, vertex);
                dfsEdges(vertex);
            }
        }

    }

    public OnGraphTraverse getOnGraphTraverse() {
        return mOnGraphTraverse;
    }

    public void setOnGraphTraverse(OnGraphTraverse mOnGraphTraverse) {
        this.mOnGraphTraverse = mOnGraphTraverse;
    }

    /**
     * Contains a set of unique paths regardless of direction. This means that a path from Table1 to Table2
     * is the same as the path from Table2 to Table1. When inserting a path, it will check if there is an already
     * existent path regardless of direction, then no new path will be added. Also, verifying whether a path exists
     * does not consider order of the starting and end points.
     */
    class GraphPaths{
        HashMap<MyType, HashSet<MyType>> paths;

        protected GraphPaths(){
            paths = new HashMap<MyType, HashSet<MyType>>();
        }

        protected boolean containsPath(MyType t1, MyType t2){
            boolean pathFound = false;

            HashSet<MyType> fromNode = paths.get(t1);
            if(fromNode != null){
                //Since there is a path from t1 to other nodes, check if there there is a path to t2
                //by searching for t2 in the t1's HashSet. If yes, then return true (there is already a path)
                pathFound = fromNode.contains(t2);
            }else{
                //There are no paths from t1, but check if there are paths from t2, and whether one of those paths leads
                // to t1.
                fromNode = paths.get(t2);
                if(fromNode != null){
                    pathFound = fromNode.contains(t1);
                }
            }

            return pathFound;
        }

        /**
         * Adds a path if it does not exist; t1 to t2 is the same path as t2 to t1.
         * @param t1
         * @param t2
         */
        protected void addPath(MyType t1, MyType t2){
            HashSet<MyType> fromNode = paths.get(t1);
            if(fromNode != null){
                fromNode.add(t2); //Since fromNode is a hashset (a set), there are no duplicates.
            }else if((fromNode = paths.get(t2)) != null){
                fromNode.add(t1); //Since fromNode is a hashset (a set), there are no duplicates.
            }else{
                fromNode = new HashSet<MyType>();
                fromNode.add(t2);
                paths.put(t1, fromNode);
            }
        }

        /**
         * Removes an existent path, if it exists.
         * @param t1
         * @param t2
         */
        protected void removePath(MyType t1, MyType t2){
            HashSet<MyType> fromNode = paths.get(t1);
            if(fromNode != null){
                fromNode.remove(t2);
                if(fromNode.size() == 0){
                    paths.remove(t1);
                }

            }else if((fromNode = paths.get(t2)) != null){
                fromNode.remove(t1);
                if(fromNode.size() == 0){
                    paths.remove(t2);
                }
            }
        }

        protected void clear(){
            paths.clear();
        }

    }


    public interface OnGraphTraverse<MyType> {
        void onDfsNewEdgeTraveled(MyType from, MyType to);
        void onDfsUnconnectedNodeFound(MyType node);
        void onDfsNewNodeVisited(MyType node);
    }

}
