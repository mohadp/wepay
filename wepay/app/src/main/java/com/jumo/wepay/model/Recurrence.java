package com.jumo.wepay.model;

/**
 * Created by Moha on 6/26/15.
 */
public class Recurrence {

    //Constants
    public static final int PERIOD_DAILY = 0;
    public static final int PERIOD_WEEKLY = 1;
    public static final int PERIOD_MONTHLY = 2;

    public static final int OFFSET_LAST_DAY_OF_MONTH = -1;


    private long id;
    private long periodicity;

    private long offset;

    public String toString(){
        StringBuilder toString = new StringBuilder("Recurrence: {");
        toString.append(id).append(", ")
                .append(periodicity).append(", ")
                .append(offset).append("}");
        return toString.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(long periodicity) {
        this.periodicity = periodicity;
    }

    /**
     * When periodicity is
     *  PERIODICITY_DAILY, this variable has no significance;
     *  PERIODICITY_WEEKLY, offset = 1 means Monday, 2 is Tuesday, ... , 7 is Sunday.
     *  PERIODICITY_MONTHLY, offset = 1 means the first of the month, offset = OFFSET_LAST_OF_MONTH means last day of every month.
     **/
    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
}
