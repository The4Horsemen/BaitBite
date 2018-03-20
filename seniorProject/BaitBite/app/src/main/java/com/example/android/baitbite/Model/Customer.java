package com.example.android.baitbite.Model;

public class Customer {
    private String Name;
    private String Password;

    public Customer() {
    }

    public Customer(String Name, String Password) {
        this.Name = Name;
        this.Password = Password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
