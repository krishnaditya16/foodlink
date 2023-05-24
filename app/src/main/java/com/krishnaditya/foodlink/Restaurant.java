package com.krishnaditya.foodlink;

public class Restaurant {

    private String name;
    private String description;
    private String imageRestaurant;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Restaurant(String name, String description, String imageRestaurant) {
        this.name = name;
        this.description = description;
        this.imageRestaurant = imageRestaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageRestaurant() {
        return imageRestaurant;
    }

    public void setImageRestaurant(String imageRestaurant) {
        this.imageRestaurant = imageRestaurant;
    }
}

