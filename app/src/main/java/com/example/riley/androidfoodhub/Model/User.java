package com.example.riley.androidfoodhub.Model;

/**
 * Created by riley on 12/9/2017.
 */

public class User {
    private String name;
    private String pass;
    private String Phone;
    private String IsStaff;
    private String secureCode;
    private String homeAddress;
    private double balance;

    public User() {
    }

    public User(String name, String pass, String secureCode) {
        this.name = name;
        this.pass = pass;
        balance = 0.0;
        IsStaff = "false";
        this.secureCode = secureCode;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
