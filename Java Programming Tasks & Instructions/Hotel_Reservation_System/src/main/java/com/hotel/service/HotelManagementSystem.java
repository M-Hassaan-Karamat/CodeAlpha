package com.hotel.service;

import com.hotel.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HotelManagementSystem {
    private List<Room> rooms;
    private List<Reservation> reservations;
    private static final String ROOMS_FILE = "hotel_rooms.dat";
    private static final String RESERVATIONS_FILE = "reservations.dat";

    public HotelManagementSystem() {
        this.rooms = new ArrayList<>();
        this.reservations = new ArrayList<>();
        initializeRooms();
        loadData();
    }

    private void initializeRooms() {
        // Add some default rooms if none exist
        if (rooms.isEmpty()) {
            addRoom(new Room(101, RoomType.STANDARD, 99.99));
            addRoom(new Room(102, RoomType.STANDARD, 99.99));
            addRoom(new Room(201, RoomType.DELUXE, 149.99));
            addRoom(new Room(202, RoomType.DELUXE, 149.99));
            addRoom(new Room(301, RoomType.SUITE, 249.99));
            addRoom(new Room(401, RoomType.EXECUTIVE, 349.99));
            addRoom(new Room(501, RoomType.PRESIDENTIAL, 999.99));
        }
    }\n
    public void addRoom(Room room) {
        rooms.add(room);
        saveRooms();
    }

    public List<Room> searchAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        return rooms.stream()
                .filter(room -> type == null || room.getType() == type)
                .filter(Room::isAvailable)
                .filter(room -> isRoomAvailable(room.getRoomNumber(), checkIn, checkOut))
                .collect(Collectors.toList());
    }

    public Reservation makeReservation(String guestName, String guestEmail, int roomNumber, 
                                     LocalDate checkIn, LocalDate checkOut) {
        Room room = findRoomByNumber(roomNumber);
        if (room == null || !room.isAvailable() || !isRoomAvailable(roomNumber, checkIn, checkOut)) {
            return null;
        }

        Reservation reservation = new Reservation(guestName, guestEmail, room, checkIn, checkOut);
        reservations.add(reservation);
        room.setAvailable(false);
        
        saveReservations();
        saveRooms();
        
        return reservation;
    }

    public boolean cancelReservation(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId) && !r.isCancelled())
                .findFirst()
                .map(reservation -> {
                    reservation.cancel();
                    saveReservations();
                    saveRooms();
                    return true;
                })
                .orElse(false);
    }

    public boolean processPayment(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId) && !r.isCancelled())
                .findFirst()
                .map(reservation -> {
                    reservation.processPayment();
                    saveReservations();
                    return true;
                })
                .orElse(false);
    }

    public Optional<Reservation> findReservation(String reservationId) {
        return reservations.stream()
                .filter(r -> r.getReservationId().equals(reservationId))
                .findFirst();
    }

    private Room findRoomByNumber(int roomNumber) {
        return rooms.stream()
                .filter(room -> room.getRoomNumber() == roomNumber)
                .findFirst()
                .orElse(null);
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return reservations.stream()
                .filter(r -> r.getRoom().getRoomNumber() == roomNumber && !r.isCancelled())
                .noneMatch(r -> !checkOut.isBefore(r.getCheckInDate()) && !checkIn.isAfter(r.getCheckOutDate()));
    }

    // File I/O Operations
    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ROOMS_FILE))) {
            rooms = (List<Room>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No existing room data found. Starting with default rooms.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading room data: " + e.getMessage());
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(RESERVATIONS_FILE))) {
            reservations = (List<Reservation>) ois.readObject();
            // Update room availability based on active reservations
            reservations.stream()
                    .filter(r -> !r.isCancelled())
                    .forEach(r -> r.getRoom().setAvailable(false));
        } catch (FileNotFoundException e) {
            System.out.println("No existing reservation data found.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading reservation data: " + e.getMessage());
        }
    }

    private void saveRooms() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.out.println("Error saving room data: " + e.getMessage());
        }
    }

    private void saveReservations() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RESERVATIONS_FILE))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.out.println("Error saving reservation data: " + e.getMessage());
        }
    }
}
