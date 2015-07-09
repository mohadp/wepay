package com.jumo.wepay.model;

import java.util.HashMap;

/**
 * Created by Moha on 6/28/15.
 */
public class Categories {

    //Singleton instance of Categories;
    private static Categories categories;

    private HashMap<Integer, String> categoryItems;

    private Categories(){
        //Categories could be added from a file so that there is no need to modify source code to add new categories.
        //A new getInstance and constructor could be created to accept a file containing categories (could be a JSON file)
        categoryItems = new HashMap<Integer, String>();

        categoryItems.put(1, "ATM");
        categoryItems.put(2, "Bills");
        categoryItems.put(3, "Education");
        categoryItems.put(4, "Entertainment");
        categoryItems.put(5, "Fees");
        categoryItems.put(6, "Food");
        categoryItems.put(7, "Health");
        categoryItems.put(8, "Home");
        categoryItems.put(9, "Kids");
        categoryItems.put(10, "Payment");
        categoryItems.put(11, "Pets");
        categoryItems.put(12, "Transportation");
        categoryItems.put(13, "Travel");
        categoryItems.put(14, "Shopping");
    }

    public static Categories getInstance(){
        initialize();
        return categories;
    }

    public static void initialize(){
        if(categories == null){
            categories = new Categories();
        }
    }

    public static String get(int categoryId){
        initialize();
        return categories.categoryItems.get(categoryId);
    }

}
