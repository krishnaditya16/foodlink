package com.krishnaditya.foodlink;

public class Rating {

    private String rating;
    private String notes;
    private String userId;
    private String menuId;

    private String orderId;

    public Rating() {
        // Default constructor required for calls to DataSnapshot.getValue(Rating.class)
    }

    public Rating(String rating, String notes, String userId, String menuId, String orderId) {
        this.rating = rating;
        this.notes = notes;
        this.userId = userId;
        this.menuId = menuId;
        this.orderId = orderId;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
