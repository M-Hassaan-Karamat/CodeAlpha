package com.hotel.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private String reservationId;
    private String guestName;
    private String guestEmail;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;
    private boolean isPaid;
    private boolean isCancelled;

    public Reservation(String guestName, String guestEmail, Room room, 
                      LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = calculateTotalPrice();
        this.isPaid = false;
        this.isCancelled = false;
    }

    private double calculateTotalPrice() {
        long nights = checkInDate.datesUntil(checkOutDate).count();
        return room.getPricePerNight() * nights;
    }

    public void processPayment() {
        this.isPaid = true;
    }

    public void cancel() {
        this.isCancelled = true;
        this.room.setAvailable(true);
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getGuestEmail() { return guestEmail; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public boolean isPaid() { return isPaid; }
    public boolean isCancelled() { return isCancelled; }

    @Override
    public String toString() {
        return String.format("""
                Reservation ID: %s
                Guest: %s (%s)
                Room: %d - %s
                Check-in: %s
                Check-out: %s
                Total: $%.2f
                Status: %s%s""",
                reservationId, guestName, guestEmail,
                room.getRoomNumber(), room.getType(),
                checkInDate, checkOutDate, totalPrice,
                isCancelled ? "CANCELLED" : (isPaid ? "PAID" : "PENDING PAYMENT"),
                isCancelled ? "" : "\n                Payment: " + (isPaid ? "COMPLETED" : "PENDING"));
    }
}
