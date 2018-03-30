package com.example.android.baitbite.Model;

public class Order {
    private String DishID;
    private String DishName;
    private String Quantity;
    private String Price;
    private String Discount;

    public Order() {
    }

    public Order(String dishID, String dishName, String quantity, String price, String discount) {
        DishID = dishID;
        DishName = dishName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public String getDishID() {
        return DishID;
    }

    public void setDishID(String dishID) {
        DishID = dishID;
    }

    public String getDishName() {
        return DishName;
    }

    public void setDishName(String dishName) {
        DishName = dishName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
