package com.jumo.wepay.provider;

/**
 * Created by Moha on 9/7/15.
 */
class Column {
    public String name;
    public String datatype;
    public String spec;
    public String table;

    private Column(String dt, String def) {
        datatype = dt;
        spec = def;
    }

    private Column(String n, String dt, String def) {
        this(dt, def);
        name = n;
    }

    public Column(String t, String n, String dt, String def) {
        this(n, dt, def);
        table = t;
    }

    public String getFullName() {
        StringBuffer sb = (new StringBuffer(table)).append(".").append(name);
        return sb.toString();
    }
}
