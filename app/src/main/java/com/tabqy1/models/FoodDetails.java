package com.tabqy1.models;

import java.util.List;

/**
 * Created by RamSingh on 11/30/2016.
 */
public class FoodDetails {

    public String name;
    public String unit_price;
    public String qty;
    public String price;
    public String food_image;
    public String description;
    public String categories;

    public List<OrderAssociateFood> associated_food;

}
