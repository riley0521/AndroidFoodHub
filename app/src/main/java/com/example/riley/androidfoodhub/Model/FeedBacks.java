package com.example.riley.androidfoodhub.Model;

/**
 * Created by ACER on 2/27/2018.
 */

public class FeedBacks {
    private String phone;
    private String feedbacks;

    public FeedBacks() {
    }

    public FeedBacks(String phone, String feedbacks) {
        this.phone = phone;
        this.feedbacks = feedbacks;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(String feedbacks) {
        this.feedbacks = feedbacks;
    }
}
