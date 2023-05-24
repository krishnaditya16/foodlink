package com.krishnaditya.foodlink;

public class Reservation {
    private String restaurantId;
    private String userId;
    private String date;
    private String time;
    private String person;
    private String notes;

    public Reservation() {
        // Default constructor required for calls to DataSnapshot.getValue(Reservation.class)
    }

    public Reservation(String restaurantId, String userId, String date, String time, String person, String notes) {
        this.restaurantId = restaurantId;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.person = person;
        this.notes = notes;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
