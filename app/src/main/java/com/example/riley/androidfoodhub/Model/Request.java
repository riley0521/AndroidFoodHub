package com.example.riley.androidfoodhub.Model;

import java.util.List;

/**
 * Created by riley on 12/13/2017.
 */

public class Request {
    private String currentDate;
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String latLng;
    private String paymentMethod;
    private String paymentState;
    private String restaurantId;
    private List<Order> foods;

    public Request() {
    }

    public Request(String currentDate, String phone, String name, String address, String total, String status, String comment, String latLng, String paymentMethod, String paymentState, String restaurantId, List<Order> foods) {
        this.currentDate = currentDate;
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.latLng = latLng;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        this.restaurantId = restaurantId;
        this.foods = foods;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
