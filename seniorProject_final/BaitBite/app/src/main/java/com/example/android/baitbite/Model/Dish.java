package com.example.android.baitbite.Model;

/**
 * Created by janbi on 3/24/2018.
 */

public class Dish {
    private String Name, Image, Description, Price, Discount, CategoryId, ChefId,  Quantity;

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
        this.ChefId = "";
        this.Quantity = "0";
    }

    public String getChefId() {
        return ChefId;
    }

    public void setChefId(String chefId) {
        ChefId = chefId;
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
