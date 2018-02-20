package com.tabqy1.models;

/**
 * Created by RamSingh on 11/16/2016.
 */

public class Waitertable {

    public String table_id;
    public String resturant_id;
    public String table_name;
    public String isBooked  ;

    @Override
    public String toString() {
        return this.table_name;
    }
}
