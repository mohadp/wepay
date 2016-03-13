package com.jumo.tablas.provider.dao;

import com.jumo.tablas.provider.TablasContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Moha on 9/7/15.
 */
public abstract class CompositeTable extends Table {

    private static final String TAG = "CompositeTable";
    private LinkedHashSet<Table> mTables; //we want to keep the tables in the order in which they were imported
    private HashMap<String, Metric> mMetrics; //we want to keep track of the metrics for this composite tables.


    public static final String _ID = "_id";

    protected CompositeTable() {
        super();        //mTableGraph = buildGraph(mTables);

    }

    protected CompositeTable(String tableName){
        super(tableName);
    }

    private void instantiateMembers(){
        mTables = new LinkedHashSet<Table>();
        mMetrics = new LinkedHashMap<String, Metric>();

    }

    /**
     * Determine the mTables that conform the CompositeTable by adding references to the mTables in the "mTables" attribute.
     * Having all mTables in one collection will allow the querying layer to be able to join the mTables correctly.
     * Order in which tables are added to the mTables collection determines the order in which tables are joined.
     */
    protected abstract void addTablesToCompositeTable();

    /**
     * Includes metrics to this composite table definition. The metrics should be added with the addMetric method.
     */
    protected abstract void addMetrics();

    /**
     * Determines the column that will represent the ID of this composite table (generally the lowest-level table).
     * @return
     */
    protected abstract Column getIdColumn();


    /**
     * Implementation of the define table; this implementation does the following:
     * - Adds all the tables that make up this composite table (when adding tables,
     *   this table's columns are added to the composite table' columns.
     * - Determines the ID column of this composite table.
     */
    protected void defineTable() {
        instantiateMembers();
        addTablesToCompositeTable();
        addColumn(_ID, getIdColumn());
        addMetrics();
    }


    /**
     * Returns the metric for the provided column name
     * @param colName
     * @return
     */
    public Metric getMetric(String colName){
        return (mMetrics == null)? null : mMetrics.get(colName);
    }


    /**
     * Key in the hash map is the column name. The value contains a Column object with all its information.
     */
    /*@Override
    public Collection<Column> getColumns() {
        ArrayList<Column> cols = new ArrayList<Column>();
        for (Table e : mTables) {
            cols.addAll(e.getColumns());
        }
        return cols;
    }*/

    /*@Override
    public Collection<String> getColumnNames() {
        ArrayList<String> cols = new ArrayList<String>();
        for (Table e : mTables) {
            for (Column c : e.getColumns()) {
                cols.add(c.getFullName());
            }
        }
        return cols;
    }*/

    /*
     * Returns the Column object that represents the full column name (ful column name is in the form of table.column)
     * @param fullColName
     * @return
     */
    /*@Override
    public Column getColumn(String fullColName) {
        String[] colfullName = fullColName.split("\\.");

        if (colfullName.length != 2)
            return null;

        Table table = TablasContract.getTable(colfullName[0]);
        if(table == null || !mTables.contains(table)) //the table of the column is not in this composite table
            return null;

        return table.getColumn(colfullName[1]);
    }*/


    /*@Override
    public String getFullColumnName(String fullColName) {
        return fullColName;
    }*/

    public HashSet<Table> tables() {
        return mTables;
    }

    /**
     * Adds the a table to this composite table if it hasn't been added, and also adds this  table's columns to this composite table, referred to it with the columns full column name.
     *
     * @param table
     */
    public void addTable(Table table) {
        if(!mTables.contains(table)){
            mTables.add(table);
            addColumns(table.getColumnMap());
            //The _id column is present in all tables. To keep this one in this composite table, we'll save these "_id" columns in the form of "tableName._id".
            Column idColumn = table.getColumn(Table._ID);
            if(idColumn != null){
                addColumn(idColumn.getFullName(), idColumn);
            }
        }
    }

    /**
     * Adds a metric to this composite table; this involves the following actions:
     * - Adding all the dependent tables (and their columns, using the addTables method) required to calculate this metric to this composite table.
     * - Adding this metric's column to this composite table columns, and relates it to its metric's column in the mMetrics map.
     *
     * A metric is not added to this composite table if any of the following is true:
     * - the metric is already added (based on metric name, which is the metric's column name)
     * - it does not have any dependent tables or
     * - it does not have a column representing this metric, for example.
     *
     * @param metric the metric object to add.
     * @return true if the metric was added, or false if it wasn't.
     *
     */
    public boolean addMetric(Metric metric){
        ArrayList<Table> dependentTables = metric.getDependentTables();
        Column metricCol = metric.getColumn();
        if(dependentTables == null || metricCol == null || metricCol.name == null || mMetrics.containsKey(metricCol.name))
            return false;

        for(Table t : dependentTables){
            addTable(t);
        }
        addColumn(metricCol.name, metricCol);
        mMetrics.put(metric.getColumnName(), metric);

        return true;
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

    /**
     * Creates a join tree with the set of tables that compose the composite table. If
     * additional base entitites are passed on, then these are also considered in the join tree.
     *
     * @param additionalTables this may be null or an arraylist of tables; null will add no additional tables to the ones that conform the composite table.
     * @return the root of the join tree.
     */
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

}
