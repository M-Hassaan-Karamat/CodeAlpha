package com.hotel;

import com.hotel.model.RoomType;
import com.hotel.model.Room;
import com.hotel.service.HotelManagementSystem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    private static final HotelManagementSystem hotelSystem = new HotelManagementSystem();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("=== Hotel Reservation System ===\n");
        
        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Make a Reservation");
            System.out.println("3. View/Cancel Reservation");
            System.out.println("4. Process Payment");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                switch (choice) {
                    case 1 -> searchRooms();
                    case 2 -> makeReservation();
                    case 3 -> viewOrCancelReservation();
                    case 4 -> processPayment();
                    case 5 -> {
                        System.out.println("Thank you for using the Hotel Reservation System!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void searchRooms() {
        System.out.println("\n=== Search Available Rooms ===");
        
        RoomType roomType = selectRoomType(true);
        LocalDate checkIn = getDateInput("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = getDateInput("Enter check-out date (YYYY-MM-DD): ");
        
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            System.out.println("Invalid date range. Please try again.");
            return;
        }
        
        List<Room> availableRooms = hotelSystem.searchAvailableRooms(roomType, checkIn, checkOut);
        
        if (availableRooms.isEmpty()) {
            System.out.println("\nNo rooms available for the selected criteria.");
        } else {
            System.out.println("\nAvailable Rooms:");
            System.out.println("--------------------------------------------------");
            availableRooms.forEach(System.out::println);
            System.out.println("--------------------------------------------------");
        }
    }

    private static void makeReservation() {
        System.out.println("\n=== Make a Reservation ===");
        
        System.out.print("Enter guest name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter guest email: ");
        String email = scanner.nextLine().trim();
        
        RoomType roomType = selectRoomType(false);
        LocalDate checkIn = getDateInput("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkOut = getDateInput("Enter check-out date (YYYY-MM-DD): ");
        
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            System.out.println("Invalid date range. Please try again.");
            return;
        }
        
        List<Room> availableRooms = hotelSystem.searchAvailableRooms(roomType, checkIn, checkOut);
        
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected criteria.");
            return;
        }
        
        System.out.println("\nAvailable " + roomType + " Rooms:");
        availableRooms.forEach(room -> System.out.println("Room " + room.getRoomNumber() + 
                " - $" + room.getPricePerNight() + "/night"));
        
        System.out.print("\nEnter room number to book: ");
        try {
            int roomNumber = Integer.parseInt(scanner.nextLine().trim());
            
            var reservation = hotelSystem.makeReservation(name, email, roomNumber, checkIn, checkOut);
            
            if (reservation != null) {
                System.out.println("\nReservation Successful!");
                System.out.println(reservation);
                System.out.println("\nPlease complete your payment to confirm the booking.");
            } else {
                System.out.println("Failed to make reservation. Room might not be available.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid room number. Please try again.");
        }
    }

    private static void viewOrCancelReservation() {
        System.out.println("\n=== View/Cancel Reservation ===");
        System.out.print("Enter reservation ID: ");
        String reservationId = scanner.nextLine().trim();
        
        hotelSystem.findReservation(reservationId).ifPresentOrElse(
            reservation -> {
                System.out.println("\nReservation Details:");
                System.out.println("-----------------------------");
                System.out.println(reservation);
                System.out.println("-----------------------------");
                
                if (!reservation.isCancelled()) {
                    System.out.print("\nDo you want to cancel this reservation? (yes/no): ");
                    String choice = scanner.nextLine().trim().toLowerCase();
                    
                    if (choice.equals("yes") || choice.equals("y")) {
                        if (hotelSystem.cancelReservation(reservationId)) {
                            System.out.println("Reservation has been cancelled successfully.");
                        } else {
                            System.out.println("Failed to cancel reservation. It might already be cancelled or paid.");
                        }
                    }
                }
            },
            () -> System.out.println("Reservation not found.")
        );
    }

    private static void processPayment() {
        System.out.println("\n=== Process Payment ===");
        System.out.print("Enter reservation ID: ");
        String reservationId = scanner.nextLine().trim();
        
        hotelSystem.findReservation(reservationId).ifPresentOrElse(
            reservation -> {
                if (reservation.isCancelled()) {
                    System.out.println("This reservation has been cancelled and cannot be paid for.");
                    return;
                }
                
                if (reservation.isPaid()) {
                    System.out.println("This reservation has already been paid for.");
                    return;
                }
                
                System.out.println("\nPayment Details:");
                System.out.println("-----------------------------");
                System.out.println("Reservation ID: " + reservation.getReservationId());
                System.out.println("Guest: " + reservation.getGuestName());
                System.out.println("Room: " + reservation.getRoom().getRoomNumber() + " - " + 
                                 reservation.getRoom().getType());
                System.out.printf("Total Amount: $%.2f%n", reservation.getTotalPrice());
                System.out.println("-----------------------------");
                
                System.out.print("\nEnter card number (16 digits): ");
                String cardNumber = scanner.nextLine().trim();
                
                if (cardNumber.matches("\\d{16}")) {
                    System.out.print("Enter expiry date (MM/YY): ");
                    String expiry = scanner.nextLine().trim();
                    
                    if (expiry.matches("\\d{2}/\\d{2}")) {
                        System.out.print("Enter CVV: ");
                        String cvv = scanner.nextLine().trim();
                        
                        if (cvv.matches("\\d{3,4}")) {
                            // Simulate payment processing
                            System.out.println("\nProcessing payment...");
                            try {
                                Thread.sleep(1500); // Simulate processing time
                                
                                if (hotelSystem.processPayment(reservationId)) {
                                    System.out.println("\nPayment successful! Your booking is confirmed.");
                                    System.out.println("A confirmation has been sent to: " + reservation.getGuestEmail());
                                } else {
                                    System.out.println("Payment processing failed. Please try again.");
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                System.out.println("Payment processing was interrupted. Please try again.");
                            }
                        } else {
                            System.out.println("Invalid CVV. Payment cancelled.");
                        }
                    } else {
                        System.out.println("Invalid expiry date format. Use MM/YY. Payment cancelled.");
                    }
                } else {
                    System.out.println("Invalid card number. Payment cancelled.");
                }
            },
            () -> System.out.println("Reservation not found.")
        );
    }

    private static RoomType selectRoomType(boolean includeAll) {
        System.out.println("\nSelect Room Type:");
        if (includeAll) {
            System.out.println("0. All Types");
        }
        RoomType[] types = RoomType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("%d. %s%n", i + 1, types[i]);
        }
        
        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                
                if (includeAll && choice == 0) {
                    return null; // Return null to indicate all types
                }
                
                if (choice > 0 && choice <= types.length) {
                    return types[choice - 1];
                }
                
                System.out.println("Invalid choice. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String dateStr = scanner.nextLine().trim();
                return LocalDate.parse(dateStr, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
    }
}
