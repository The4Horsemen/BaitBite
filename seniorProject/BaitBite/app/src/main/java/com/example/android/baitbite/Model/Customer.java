package com.example.android.baitbite.Model;

import java.util.UUID;

public class Customer {

    //private String Account_ID;
    private String Email;
    private int LocationX;
    private int LocationY;
    private int Mobile_Verification_Code;
    private String Name;
    private String Phone;
    // delete this part after testing
    private String Password;

    public void setPassword(String password) {
        Password = password;
    }
    public String getPassword() {
        return Password;
    }

    //..........................................
    public Customer() {
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

    public int getMobile_Verification_Code() {
        return Mobile_Verification_Code;
    }

    public void setMobile_Verification_Code(int mobile_Verification_Code) {
        Mobile_Verification_Code = mobile_Verification_Code;
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
