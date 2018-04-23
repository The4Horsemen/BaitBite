package com.example.android.baitbite.Model;

/**
 * Created by janbi on 3/24/2018.
 */

public class Dish {
    private String Name, Image, Description, Price, Discount, CategoryId, ChefID,  Quantity;

    public Dish(String name, String image, String description, String price, String discount, String categoryId) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        CategoryId = categoryId;

    }

    public Dish() {
        this.Name = "";
        this.Image = "";
        this.Description ="";
        this.Price = "";
        this.Discount = "";
        this.CategoryId="";
        this.ChefID = "";
        this.Quantity = "0";
    }

    public Dish(String name, String image, String description, String price, String discount, String categoryID, String chefID) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Discount = discount;
        CategoryId = categoryID;
        ChefID = chefID;
        this.Quantity = "0";
    }

    public String getChefID() {
        return ChefID;
    }

    public void setChefID(String chefID) {
        ChefID = chefID;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
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
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }
}
