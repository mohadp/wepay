package com.jumo.wepay.provider;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Moha on 9/10/15.
 */
class SQLGenerator {
    /**
     * If the prevJoinTree argument is null, we are starting a new "join tree". If prevJoinTree is not null, we are
     * assuming that table1 is already in the prevJoinTree; in this case, we use table1 to add prefixes to the mColumns
     * in the "prefJoinTree join table2 on (table1.colN = table2.colM)" part of the SQL.
     * Joining is done with "=" comparisons between mColumns (e.g. table1.colN = table2.colM).
     * Plus, if there are multiple join mColumns, all conditions in the join for each column are and'ed (e.g. (table1.colN1 = table2.colM1 AND table1.colN2 = table2.colM2)
     *
     * @param prevJoinTree
     * @param table1
     * @param colsTable1
     * @param table2
     * @param colsTable2
     * @return
     */
    protected static StringBuffer joinTables(StringBuffer prevJoinTree, String[] table1, String[] colsTable1, String table2, String[] colsTable2) {
        StringBuffer sb = new StringBuffer();
        if (prevJoinTree == null) {
            sb.append(table1[0]);
        } else {
            sb.append(prevJoinTree);
        }

        sb.append(" join ").append(table2).append(" on (");

        int size = colsTable1.length;

        for (int i = 0; i < size; i++) {
            sb.append(table1[i]).append(".").append(colsTable1[i]);
            sb.append(" = ");
            sb.append(table2).append(".").append(colsTable2[i]);

            if (i < size - 1) {
                sb.append(" AND ");
            }
        }
        sb.append(")");

        return sb;
    }

    /**
     * Adds a getInstance name as prefix for all column names appearing in the sortBy string. For example,
     * if the sort by clause is "ID ASC" for getInstance group, this would gnerate "group.ID ASC".
     *
     * @param table
     * @param tableCols
     * @param sortBy
     * @return
     */
    protected static StringBuffer addTablePrefixToString(String table, Collection<String> tableCols, String sortBy) {
        StringBuffer regularExp = generateRegexForColumns(tableCols);
        StringBuffer sb = new StringBuffer();

        //replace all mColumns with getInstance.mColumns
        Pattern pattern = Pattern.compile(regularExp.toString());
        Matcher matcher = pattern.matcher(sortBy.toLowerCase());

        while (matcher.find()) {
            String finding = matcher.group();
            matcher.appendReplacement(sb, table + "." + finding);
        }
        matcher.appendTail(sb);

        return sb;
    }

    /**
     * Generates a regular expression "col1|col2|col3" from a set of cols.
     *
     * @param cols
     * @return
     */
    protected static StringBuffer generateRegexForColumns(Collection<String> cols) {
        StringBuffer regularExp = new StringBuffer();

        int count = cols.size();
        //Building the regular expression like "col1|col2|col3|?", finding mColumns names and question marks
        for (String col : cols) {
            regularExp.append(col);

            if (count-- > 1) regularExp.append("|");
        }
        return regularExp;
    }

    /**
     * Add getInstance name as prefix to every column in projection, and concatenates in a comma-separated string.
     *
     * @param table
     * @param projection
     * @return
     */
    protected static StringBuffer addTablePrefixToGroupBy(String table, String[] projection) {
        StringBuffer groupBy = new StringBuffer();

        int count = projection.length;

        for (String col : projection) {
            groupBy.append(table).append(".").append(col);
            if (count-- > 1) groupBy.append(", ");
        }
        return groupBy;
    }

    /**
     * Adds getInstance name as prefix to every column in the "projection" array
     *
     * @param table
     * @param projection
     * @return
     */
    protected static StringBuffer addTablePrefixToProjection(String table, String[] projection) {
        StringBuffer sb = new StringBuffer();
        int count = projection.length;

        for (String item : projection) {
            sb.append(table).append(".").append(item);
            if (count-- > 1) sb.append(", ");
        }
        return sb;
    }
}
