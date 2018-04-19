package com.example.matsah.baitbite_chef.Model;

public class Customer {

    //private String Account_ID;
    private String Email;
    private int LocationX;
    private int LocationY;
    private String Name;
    private String Phone;
    private String Password;
    private String Profile_Image;
    private String creditCard;


    public void setPassword(String password) {
        Password = password;
    }
    public String getPassword() {
        return Password;
    }

    //..........................................


    public Customer() {
        Email = "";
        LocationX = 0;
        LocationY = 0;
        Name = "";
        Phone = "";
        Password = "";
        Profile_Image = "";
    }

    public Customer(String Name) {
        this.Name = Name;
    }

    public Customer(String name, String phone) {
        Name = name;
        Phone = phone;
    }

    public Customer(String email, int locationX, int locationY, String name, String phone){
        this.Email = email;
        this.LocationX = locationX;
        this.LocationY = locationY;
        this.Name = name;
        this.Phone = phone;
        this.Profile_Image = "";

    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        Profile_Image = profile_Image;
    }

    public void Get_GPS_Location() {

    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public int getLocationX() {
        return LocationX;
    }

    public void setLocationX(int locationX) {
        LocationX = locationX;
    }
    public int getLocationY() {
        return LocationY;
    }

    public void setLocationY(int locationY) {
        LocationY = locationY;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }









}
