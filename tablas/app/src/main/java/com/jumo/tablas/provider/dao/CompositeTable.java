package com.jumo.tablas.provider.dao;

import com.jumo.tablas.provider.TablasContract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Moha on 9/7/15.
 */
public abstract class CompositeTable extends Table {

    private static final String TAG = "CompositeTable";
    private LinkedHashSet<Table> mTables; //we want to keep the tables in the order in which they were imported
    private LinkedHashMap<Table, LinkedHashSet<Table>> mTableGraph;

    protected CompositeTable() {
        super();
        mTables = new LinkedHashSet<Table>();
        defineTables();
        //mTableGraph = buildGraph(mTables);
    }

    /**
     * Determine the mTables that conform the CompositeTable by adding references to the mTables in the "mTables" attribute.
     * Having all mTables in one collection will allow the querying layer to be able to join the mTables correctly.
     * Order in which tables are added to the mTables collection determines the order in which tables are joined.
     */
    protected abstract void defineTables();

    protected void defineColumnsAndForeignKeys() {
        mColumns = null;
        mForeignKeys = null;
        mTableName = null;
    }

    /**
     * Key in the hash map is the column name. The value contains a Column object with all its information.
     */
    @Override
    public Collection<Column> getColumns() {
        ArrayList<Column> cols = new ArrayList<Column>();
        for (Table e : mTables) {
            cols.addAll(e.getColumns());
        }
        return cols;
    }

    @Override
    public Collection<String> getColumnNames() {
        ArrayList<String> cols = new ArrayList<String>();
        for (Table e : mTables) {
            for (Column c : e.getColumns()) {
                cols.add(c.getFullName());
            }
        }
        return cols;
    }

    @Override
    public Column getColumn(String fullColName) {
        String[] colfullName = fullColName.split("\\.");

        if (colfullName.length != 2)
            return null;

        Table table = TablasContract.getTable(colfullName[0]);
        return table.getColumn(colfullName[1]);
    }

    @Override
    public String getFullColumnName(String fullColName) {
        return fullColName;
    }

    public HashSet<Table> tables() {
        return mTables;
    }

    public void addTable(Table e) {
        mTables.add(e);
    }

    /**
     * Builds the graph for the set of tables that compose this CompositeTable.
     * The vertices are the tables' foreign keys.
     */
    public static Graph<Table> buildGraph(LinkedHashSet<Table> tables){
        Graph<Table> graph = new Graph<Table>();

        //Identify all the foreign keys for each table; if table 'a' has a foregin key of table 'b'
        //then table 'a' will be connected to 'b', and 'b' will be connected to 'a' as well
        for(Table table : tables){
            Set<String> foreignTableNames = table.getForeignTables();
            if(foreignTableNames != null && foreignTableNames.size() > 0) {
                for (String foreignTableName : foreignTableNames) {
                    Table foreignTable = TablasContract.getTable(foreignTableName);
                    //only add this foreign table to graph if it is in this compositeTable table list (mTables).
                    if(tables.contains(foreignTable)) {
                        graph.addUndirectedPath(table, foreignTable);
                    }
                }
            }else{
                //Add an independent node to the graph without connections
                graph.addNode(table);
            }
        }
        return graph;
    }

    /**
     * This returns the ColumnJoins between two tables, independently of the table order. About the order
     * getEdge(table1, table2) and getEdge(table2, table1) will return the same set of ColumnJoins. If table1
     * or table2 are not in this CompositeTable' table list, this returns null;
     * @param graph the graph where the edge we will find
     * @param table1
     * @param table2
     * @return
     */
    public static LinkedHashSet<ColumnJoin> getJoinDefinition(Graph<Table> graph, Table table1, Table table2){
        Set<Table> tables = graph.getNodes();
        //if any of the tables are not part of this composit table, then return null.
        if(!tables.contains(table1) || !tables.contains(table2)){
            return null;
        }

        //if these tables are not connected, then return null, based on the graph.
        if(!graph.getNeighbors(table1).contains(table2))
            return null;

        //Knowing they are connected, then, we need to return the set of column joins between these two tables: Either
        //table1 has the foreign keys, or table2 has the forieng keys.

        return getJoinsBetweenTables(table1, table2);
    }


    public TreeNode getTableJoinTree(Set<Table> additionalTables){
        //Get the set of tables to join
        LinkedHashSet<Table> tables = new LinkedHashSet<Table>();
        tables.addAll(mTables);
        if (additionalTables != null) {
            tables.addAll(additionalTables);
        }

        final Tree tree = new Tree();
        GraphDFS.OnGraphTraverse<Table> onGraphListener = new GraphDFS.OnGraphTraverse<Table>() {
            @Override
            public void onDfsNewEdgeTraveled(Table fromTable, Table toTable) {
                addTablesToJoinTree(fromTable, toTable, tree);
            }

            @Override
            public void onDfsUnconnectedNodeFound(Table table) {
                tree.addNode(table);
            }

            @Override
            public void onDfsNewNodeVisited(Table node) { }
        };
        Graph<Table> graph = buildGraph(tables);
        GraphDFS<Table> graphDfs = new GraphDFS<Table>(graph);
        graphDfs.setOnGraphTraverse(onGraphListener);
        graphDfs.visitAllPaths(); //As the table graph is traversed the join tree is built as well.

        return tree.joinDisconnectedRoots();
    }

    /**
     * This is a support method. Adds tables to a join tree if not present in the tree, and joins them
     * in the tree. It adds JoinTreeNodes to a collection of nodes for the provided tables that conform a tree,
     * and connects them with join nodes.
     *
     * fromJoin    toJoin     Action
     *   null       null        Create new join
     *   null       notNull     Not a scenario; in DFS, fromTable will have been visited always before toTable (fromTable should have been
     *   notNull    null        Create new join (root of from, with toTable
     *   notNull    notNull     Add JoinCols in to first common parent
     */
    private void addTablesToJoinTree(Table fromTable, Table toTable, Tree tree){
        boolean fromNew = tree.addNode(fromTable);
        boolean toNew = tree.addNode(toTable);

        TreeNode fromJoin = tree.getNode(fromTable);
        TreeNode toJoin = tree.getNode(toTable);

        LinkedHashSet<ColumnJoin> columnJoins = getJoinsBetweenTables(fromTable, toTable);
        TreeNode joinNode = null;

        if(!fromNew && !toNew) {
            joinNode = TreeNode.findFirstCommonParent(fromJoin, toJoin);
        }else{
            joinNode = new TreeNode(fromJoin.getRoot(), toJoin.getRoot());
        }
        joinNode.addColumnJoins(columnJoins);
    }

    /**
     * Return the set of column joins between the two tables: Either
     * table1 has the foreign keys, or table2 has the forieng keys. If non has foreign keys of the other, this returns null.
     * @param table1
     * @param table2
     * @return
     */
    private static LinkedHashSet<ColumnJoin> getJoinsBetweenTables(Table table1, Table table2){
        LinkedHashSet<ColumnJoin> columnJoins = table1.getColumnJoinsToTable(table2.getTableName());
        if(columnJoins == null) {
            columnJoins = table2.getColumnJoinsToTable(table1.getTableName());
        }
        return columnJoins;
    }

    /**
     * Creates a join tree with the set of tables that compose the composite table. If
     * additional base entitites are passed on, then these are also considered in the join tree.
     *
     * @param additionalTables this may be null or an arraylist of tables; null will add no additional tables to the ones that conform the composite table.
     * @return the root of the join tree.
     */
    /*public TreeNode getTableJoinTree(HashSet<Table> additionalTables) {
        HashMap<String, TreeNode> tableToJoinTreeMap = new HashMap<String, TreeNode>();

        //Get the set of tables to join
        HashSet<Table> tablesToJoin = new HashSet<Table>();
        tablesToJoin.addAll(mTables);
        if (additionalTables != null) {
            tablesToJoin.addAll(additionalTables);
        }

        //Add every table, and every table's foreign tables to join tree.
        for (Table table : tablesToJoin) {
            // if a table has foreign keys, add the foreign tables to join tree as long as they are
            // part of this composite table.
            if (table.getForeignKeys() != null) {
                Set<String> foreignTables = table.getForeignKeys().keySet();
                TreeNode leftJoinNode = tableToJoinTreeMap.get(table.getTableName());
                // if the join trees for these tables are null, then create a new join trees;
                // and add them to the map to refer back to them.
                if (leftJoinNode == null) {
                    leftJoinNode = new TreeNode(table.getTableName());
                    tableToJoinTreeMap.put(table.getTableName(), leftJoinNode);
                }
                for (String foreignTableName : foreignTables) {
                    if (tablesToJoin.contains(TablasContract.getTable(foreignTableName))) {
                        TreeNode rightJoinNode = tableToJoinTreeMap.get(foreignTableName);
                        if (rightJoinNode == null) {
                            rightJoinNode = new TreeNode(foreignTableName);
                            tableToJoinTreeMap.put(foreignTableName, rightJoinNode);
                        }
                        TreeNode leftParent = leftJoinNode.getParent();
                        TreeNode rightParent = rightJoinNode.getParent();
                        TreeNode leftRoot = leftJoinNode.getRoot();
                        TreeNode rightRoot = rightJoinNode.getRoot();
                        boolean inSameTreeAlready = (leftRoot == rightRoot);

                        //Construct a tree of joins such that the left nodes in joins can be tables or other joins, and the right only tables.
                        if(leftParent == null && rightParent == null){
                            TreeNode newJoin = new TreeNode(leftJoinNode, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent != null && rightParent == null){
                            TreeNode newJoin = new TreeNode(leftRoot, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent == null && rightParent != null){
                            TreeNode newJoin = new TreeNode(rightRoot, leftJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                        }else if(leftParent != null && rightParent != null && !inSameTreeAlready){ //move right to left's tree with a new join, and make that join the left child of rightParent.
                            rightParent.switchLeft(rightJoinNode); //make sure the table to move to left's tree is on the left (we'l clear left in parent).
                            rightParent.setLeft(null);
                            TreeNode newJoin = new TreeNode(leftRoot, rightJoinNode); //this already sets the parents for the children
                            newJoin.setColumnJoins(table.getForeignKeys().get(foreignTableName));
                            rightParent.setLeft(newJoin);
                        } else if(inSameTreeAlready){
                            //Log.d(TAG, "##### In same JoinTree #####");
                            TreeNode commonParent = TreeNode.findFirstCommonParent(leftJoinNode, rightJoinNode);
                            if(commonParent != null){
                                commonParent.addColumnJoins(table.getForeignKeys().get(foreignTableName));
                            }
                        }
                    }
                }
            } else {
                //If a table has no foreign key, it will be added eventually with the tables that have references to it. At the end; if these tables end up not joined through referencing tables, they are cross joined at the end
                tableToJoinTreeMap.put(table.getTableName(), new TreeNode(table.getTableName()));
            }
        }

        //Here, we need to identify the distinct disjoint jointrees, or all the distinct roots.
        // Then, we join them through crossjoin, and return the root.
        HashSet<TreeNode> distinctJoinTrees = new HashSet<>();
        for (TreeNode jt : tableToJoinTreeMap.values()) {
            distinctJoinTrees.add(jt.getRoot());
        }

        Iterator<TreeNode> it = distinctJoinTrees.iterator();
        TreeNode root = (it.hasNext()) ? it.next() : null;
        while (it.hasNext()) {
            TreeNode xTreeNode = it.next();
            root = new TreeNode(root, xTreeNode);
        }

        return root;
    }*/
}
