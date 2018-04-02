package com.example.matsah.baitbite_chef.Model;

/**
 * Created by MATSAH on 3/29/2018.
 */

public class Chef {

    //private String Account_ID;
    private String Email ="";
    private int LocationX;
    private int LocationY;
    private int Mobile_Verification_Code;
    private String Name;
    private String Phone_Number;
    private String status ="";
    // delete this part after testing
    private String Password;
    public Chef(String Name, String Password) {
        this.Name = Name;
        this.Password = Password;

    }
    public void setPassword(String password) {
        Password = password;
    }
    public String getPassword() {
        return Password;
    }

    //..........................................
    public Chef() {
    }

    public Chef(String email, int locationX,int locationY, String name, String phone_number){
        this.Email = email;
        this.LocationX = locationX;
        this.LocationY = locationY;
        this.Name = name;
        this.Phone_Number = phone_number;

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

    public String getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        Phone_Number = phone_Number;
    }






}
