package gui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentalService {
    private static final String RENTAL_FILE_PATH = "gui/rentals.csv";
    private List<Rental> allRentals;
    private CostumeDataManager costumeManager;
    
    public RentalService() {
        this.allRentals = new ArrayList<>();
        this.costumeManager = new CostumeDataManager();
        loadRentals();
    }
    

    private void loadRentals() {
        File file = new File(RENTAL_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Rentals file not found. Creating new file: " + RENTAL_FILE_PATH);
            createEmptyRentalFile();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    Rental rental = Rental.fromCsvString(line);
                    rental.updateStatus(); 
                    allRentals.add(rental);
                } catch (Exception e) {
                    System.err.println("Error parsing rental line: " + line + " - " + e.getMessage());
                }
            }
            
            System.out.println("Loaded " + allRentals.size() + " rentals");
            
        } catch (IOException e) {
            System.err.println("Error reading rental file: " + e.getMessage());
        }
    }
    
    private void createEmptyRentalFile() {
        try {
            File file = new File(RENTAL_FILE_PATH);
            file.getParentFile().mkdirs(); 
            
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.println("# Rental Management System - Rental Records");
                writer.println("# Format: rentalId,memberId,costumeId,size,rentalDate,returnDate,actualReturnDate,totalCost,dailyRate,lateFee,status");
            }
            
        } catch (IOException e) {
            System.err.println("Error creating rental file: " + e.getMessage());
        }
    }
    
    public void saveRentals() {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(RENTAL_FILE_PATH), StandardCharsets.UTF_8))) {
            
            writer.println("# Rental Management System - Rental Records");
            writer.println("# Format: rentalId,memberId,costumeId,size,rentalDate,returnDate,actualReturnDate,totalCost,dailyRate,lateFee,status");
            
            for (Rental rental : allRentals) {
                writer.println(rental.toCsvString());
            }
            
            System.out.println("Saved " + allRentals.size() + " rentals to file");
            
        } catch (IOException e) {
            System.err.println("Error saving rental file: " + e.getMessage());
            throw new RuntimeException("Failed to save rental data", e);
        }
    }
    
    public String generateNewRentalId() {
        int maxId = 0;
        for (Rental rental : allRentals) {
            String id = rental.getRentalId();
            if (id.startsWith("R") && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                }
            }
        }
        return String.format("R%03d", maxId + 1);
    }
    

    public boolean createRental(String memberId, String costumeId, String size, LocalDate rentalDate, 
                               LocalDate returnDate, double totalCost) {
        try {
            if (!isCostumeAvailable(costumeId, size)) {
                System.err.println("Costume " + costumeId + " size " + size + " is not available for rental");
                return false;
            }
            
            String rentalId = generateNewRentalId();
            long rentalDays = calculateRentalDays(rentalDate, returnDate);
            double dailyRate = (rentalDays > 0) ? totalCost / rentalDays : totalCost;
            
            Rental newRental = new Rental(rentalId, memberId, costumeId, size, rentalDate, returnDate, totalCost, dailyRate);
            allRentals.add(newRental);
            
            updateCostumeStock(costumeId, size, -1);
            saveRentals();
            
            System.out.println("Created new rental: " + newRental);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating rental: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isCostumeAvailable(String costumeId) {
        List<Costume> costumes = costumeManager.loadCostumes();
        for (Costume costume : costumes) {
            if (costume.getCostumeId().equals(costumeId)) {
                return costume.getTotalStock() > 0;
            }
        }
        return false;
    }

    public boolean isCostumeAvailable(String costumeId, String size) {
        List<Costume> costumes = costumeManager.loadCostumes();
        for (Costume costume : costumes) {
            if (costume.getCostumeId().equals(costumeId)) {
                return costume.hasSize(size);
            }
        }
        return false;
    }
    
    private void updateCostumeStock(String costumeId, int change) {
        System.out.println("Stock update for costume " + costumeId + ": " + change);
    }
   
    private void updateCostumeStock(String costumeId, String size, int change) {
        System.out.println("Stock update for costume " + costumeId + " size " + size + ": " + change);
    }
    
    public List<Rental> getRentalsByMemberId(String memberId) {
        return allRentals.stream()
                .filter(rental -> rental.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
    
    public List<Rental> getActiveRentalsByMemberId(String memberId) {
        return allRentals.stream()
                .filter(rental -> rental.getMemberId().equals(memberId))
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.RESERVED ||
                                rental.getStatus() == Rental.RentalStatus.ACTIVE || 
                                rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    public List<Rental> getActiveRentalsByCostumeId(String costumeId) {
        return allRentals.stream()
                .filter(rental -> rental.getCostumeId().equals(costumeId))
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.ACTIVE || 
                                rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    public List<Rental> getOverdueRentals() {
        return allRentals.stream()
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    public boolean returnRental(String rentalId, LocalDate actualReturnDate) {
        for (Rental rental : allRentals) {
            if (rental.getRentalId().equals(rentalId)) {
                if (rental.getStatus() == Rental.RentalStatus.RETURNED) {
                    System.err.println("Rental " + rentalId + " is already returned");
                    return false;
                }
                
                rental.setActualReturnDate(actualReturnDate);
                rental.setStatus(Rental.RentalStatus.RETURNED);
                
                if (rental.getOverdueDays() > 0) {
                    double lateFee = rental.calculateLateFee();
                    rental.setLateFee(lateFee);
                }
                
                updateCostumeStock(rental.getCostumeId(), rental.getSize(), 1);
                saveRentals();
                
                System.out.println("Returned rental: " + rental);
                return true;
            }
        }
        System.err.println("Rental not found: " + rentalId);
        return false;
    }
    
    public boolean cancelRental(String rentalId) {
        for (Rental rental : allRentals) {
            if (rental.getRentalId().equals(rentalId)) {
                if (rental.getStatus() != Rental.RentalStatus.RESERVED) {
                    System.err.println("Cannot cancel rental " + rentalId + " with status: " + rental.getStatus());
                    return false;
                }
                
                rental.setStatus(Rental.RentalStatus.CANCELLED);
                
                updateCostumeStock(rental.getCostumeId(), rental.getSize(), 1);
                saveRentals();
                
                System.out.println("Cancelled rental: " + rental);
                return true;
            }
        }
        System.err.println("Rental not found for cancellation: " + rentalId);
        return false;
    }
    
    public void updateAllRentalStatuses() {
        boolean hasChanges = false;
        
        for (Rental rental : allRentals) {
            Rental.RentalStatus oldStatus = rental.getStatus();
            rental.updateStatus();
            
            if (oldStatus != rental.getStatus()) {
                hasChanges = true;
                System.out.println("Updated rental " + rental.getRentalId() + 
                                 " status: " + oldStatus + " -> " + rental.getStatus());
            }
        }
        
        if (hasChanges) {
            saveRentals();
        }
    }
    
    public static long calculateRentalDays(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    public static double calculateRentalCost(double dailyRate, long days) {
        return dailyRate * days;
    }
    
    public List<Rental> getAllRentals() {
        return new ArrayList<>(allRentals);
    }
   
    public Rental findRentalById(String rentalId) {
        return allRentals.stream()
                .filter(rental -> rental.getRentalId().equals(rentalId))
                .findFirst()
                .orElse(null);
    }
}