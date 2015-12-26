package com.jumo.tablas.provider.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Moha on 11/15/15.
 */
public class Tree {
    private HashMap<Table,TreeNode> tableNodes;

    public Tree(){
        tableNodes = new HashMap<Table, TreeNode>();
    }

    public TreeNode getNode(Table table){
        return tableNodes.get(table);
    }

    /**
     * Adds a table TreeNode to the tree' set of nodes if it does not exist, and returns true.
     * Else, the method returns false since no node was added.
     * @param table
     * @return
     */
    public boolean addNode(Table table){
        if(!tableNodes.keySet().contains(table)){
            TreeNode node = new TreeNode(table);
            tableNodes.put(table, node);
            return true;
        }
        return false;
    }

    public Set<TreeNode> getRoots(){
        HashSet<TreeNode> distinctJoinTrees = new HashSet<>();
        for (TreeNode node : tableNodes.values()) {
            distinctJoinTrees.add(node.getRoot());
        }
        return distinctJoinTrees;
    }


    /**
     * Returns a single-root tree that joins disconnected roots with crossjoins.
     * @return
     */
    public TreeNode joinDisconnectedRoots(){
        Set<TreeNode> roots = getRoots();
        Iterator<TreeNode> rootIterator = roots.iterator();
        TreeNode firstRoot = rootIterator.next();

        while(rootIterator.hasNext()){
            TreeNode nextRoot = rootIterator.next();
            TreeNode crossJoin = new TreeNode(firstRoot, nextRoot);
            crossJoin.setJoinType(TreeNode.CROSS_JOIN);
            firstRoot = crossJoin;
        }

        return firstRoot;
    }

}
