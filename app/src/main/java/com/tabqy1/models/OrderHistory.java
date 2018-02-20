package com.tabqy1.models;

import java.util.List;

/**
 * Created by RamSingh on 11/20/2016.
 */

public class OrderHistory {

 public String  order_no;
 public String  order_date;
 public String  order_time;
 public String  table_id;
 public String  table_name;
 public String  order_value;
 public String  waiter_name;
 public String  order_value_no_tax;
 public String  order_status;
 public String  type;
 public String  customer_status;
 public List<String> detailsList;

 @Override
 public String toString() {
  return this.order_no;
 }


}
