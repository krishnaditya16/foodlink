package com.krishnaditya.foodlink;

public class Menu {
    private String title;
    private String type;
    private String price;
    private String rating;
    private String imageMenuUrl;

    private String restaurantId;

    public Menu() {
        // Default constructor required for calls to DataSnapshot.getValue(Menu.class)
    }

    public Menu(String title, String type, String rating, String price, String imageMenuUrl, String restaurantId) {
        this.title = title;
        this.type = type;
        this.rating = rating;
        this.price = price;
        this.imageMenuUrl = imageMenuUrl;
        this.restaurantId = restaurantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageMenuUrl() {
        return imageMenuUrl;
    }

    public void setImageMenuUrl(String imageMenuUrl) {
        this.imageMenuUrl = imageMenuUrl;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

}

