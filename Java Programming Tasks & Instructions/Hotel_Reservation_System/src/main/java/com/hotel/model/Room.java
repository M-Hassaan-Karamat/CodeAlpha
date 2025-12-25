package com.hotel.model;

public class Room {
    private int roomNumber;
    private RoomType type;
    private double pricePerNight;
    private boolean isAvailable;

    public Room(int roomNumber, RoomType type, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.isAvailable = true;
    }

    // Getters and Setters
    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getType() {
        return type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return String.format("Room %d - %s - $%.2f/night - %s",
                roomNumber, type, pricePerNight, isAvailable ? "Available" : "Booked");
    }
}
