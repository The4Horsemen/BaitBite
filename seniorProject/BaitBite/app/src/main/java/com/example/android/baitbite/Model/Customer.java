package com.example.android.baitbite.Model;

/**
 * Created by janbi on 3/21/2018.
 */

public class Customer {
    private String Name;
    private String Password;

    public Customer() {
    }

    public Customer(String Name, String Password) {
        this.Name = Name;
        this.Password = Password;
    }

    public String setName(String Name) {
        this.Name = Name;
        return this.Name;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }
}
