package com.example.matsah.baitbite_chef.Model;

/**
 * Created by MATSAH on 3/28/2018.
 */

public class Dish {
    private String Name, Image, Description, Price, Discount, CategoryID, ChefID, Quantity;



    public Dish() {
        /*this.Name = "";
        this.Image = "";
        this.Description ="";
        this.Price = "";
        this.Discount = "";
        this.CategoryID="";
        this.ChefID = "";
        this.Quantity = "";*/
    }


    public Dish(String name, String image, String description, String price, String discount, String categoryID, String chefID) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        CategoryID = categoryID;
        ChefID = chefID;
        this.Quantity = "";
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        this.Quantity = quantity;
    }

    public void setChefID(String chefID) {
        ChefID = chefID;
    }

    public String getChefID() {
        return ChefID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
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

    public String getCategoryId() {
        return CategoryID;
    }

    public void setCategoryId(String categoryID) {
        CategoryID = categoryID;
    }
}
