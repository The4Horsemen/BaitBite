package com.example.matsah.baitbite_chef.Model;

/**
 * Created by MATSAH on 3/29/2018.
 */

public class Chef {

    //private String Account_ID;
    private String Phone_Number;
    private String Name;
    private String Email ="";
    private double LocationX;
    private double LocationY;
    private String Store_Summary;
    private String Profile_Image;
    private boolean Status;


    //..........................................
    /*register constructor*/
    public Chef(String Phone_Number, String Name, double LocationX, double LocationY){
        this.Phone_Number = Phone_Number;
        this.Name = Name;
        this.LocationX = LocationX;
        this.LocationY = LocationY;
        this.Store_Summary = "";
        this.Profile_Image = "";
        this.Status = false;

    }

    public Chef() {
        this.Phone_Number ="";
        this.Name = "";
        this.Email = "";
        this.LocationX = 0;
        this.LocationY = 0;
        this.Store_Summary = "";
        this.Profile_Image = "";
        this.Status = false;



    }

    public Chef(String email, double locationX,double locationY, String name, String phone_number){
        this.Email = email;
        this.LocationX = locationX;
        this.LocationY = locationY;
        this.Name = name;
        this.Phone_Number = phone_number;

    }

    public void Get_GPS_Location() {


    }


    public String getStore_Summary() {
        return Store_Summary;
    }

    public void setStore_Summary(String store_Summary) {
        Store_Summary = store_Summary;
    }

    public String getProfile_Image() {
        return Profile_Image;
    }

    public void setProfile_Image(String profile_Image) {
        this.Profile_Image = profile_Image;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        this.Status = status;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public double getLocationX() {
        return LocationX;
    }

    public void setLocationX(double locationX) {
        LocationX = locationX;
    }
    public double getLocationY() {
        return LocationY;
    }

    public void setLocationY(double locationY) {
        LocationY = locationY;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }






}
