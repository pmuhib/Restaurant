package com.tabqy1.models;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jayant on 05-11-2016.
 */

public class FoodType {

    public String foodtype_id;
    public String cusinie_id;
    public String dish_name;

    public String food_id;
    public String name;
    public String food_code;
    public String price;
    public String discount_price;
    public String description;
    public String food_image;
    public String categories;
    public int qty;
    public int itemQty;
    public String positionID;
    public boolean isSelected;
    public boolean isWithAssociated;
    public List<AssociatedFood> associated_food;
    //public List<AssociatedFood> selectedAssociatedItems= new ArrayList<>();
    public HashMap<Integer,List<AssociatedFood>> selectedAssociatedItems = new HashMap<>();


    public String food_availability_time;
    public String food_availability;
    public String food_availability_msg;





/*
    {
        "status": true,
            "hotdeals": [
        {
            "food_id": "3",
                "name": "American Soup",
                "price": "15",
                "discount_price": "00",
                "description": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                "food_image": "soup1.png",
                "food_code": "soup01",
                "categories": "American Dishes",
                "food_availability": "1",
                "food_availability_time": "10AM to 2PM",
                "associated_food": [
            {
                "associated_food_id": "1",
                    "associated_food_name": "Paratha",
                    "associated_food_price": "9",
                    "associated_food_food_code": "paratha01",
                    "associated_food_description": "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
                    "associated_food_food_availability": "1",
                    "associated_food_food_image": "baba-3.jpg",
                    "associated_food_availability_time": "0"
            }
            ]
        }
        ]
    }*/
}
