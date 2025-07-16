package gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class Rental {
    public enum RentalStatus {
        RESERVED("Reserved"),     
        ACTIVE("Active"),         
        RETURNED("Returned"),      
        OVERDUE("Overdue"),       
        CANCELLED("Cancelled");    
        
        private final String displayName;
        
        RentalStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    private String rentalId;
    private String memberId;
    private String costumeId;
    private String size;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private double totalCost;
    private double dailyRate; 
    private double lateFee;
    private RentalStatus status;


    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, double totalCost, double dailyRate) { 
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.totalCost = totalCost;
        this.dailyRate = dailyRate;
        this.lateFee = 0.0;
        this.status = RentalStatus.RESERVED;
        this.actualReturnDate = null;
        updateStatus(); 
    }
    
   
    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, LocalDate actualReturnDate,
                  double totalCost, double dailyRate, double lateFee, RentalStatus status) { 
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCost = totalCost;
        this.dailyRate = dailyRate; 
        this.lateFee = lateFee;
        this.status = status;
    }
    
    // Getter methods
    public String getRentalId() {
        return rentalId;
    }
    
    public String getMemberId() {
        return memberId;
    }
    
    public String getCostumeId() {
        return costumeId;
    }
    
    public String getSize() {
        return size;
    }
    
    public LocalDate getRentalDate() {
        return rentalDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public double getTotalCost() {
        return totalCost;
    }
    
    public double getDailyRate() { 
        return dailyRate;
    }

    public double getLateFee() {
        return lateFee;
    }
    
    public RentalStatus getStatus() {
        return status;
    }
    
    // Setter methods
    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }
    
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    
    public void setCostumeId(String costumeId) {
        this.costumeId = costumeId;
    }
    
    public void setSize(String size) {
        this.size = size;
    }
    
    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
    
    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }
    
    public void setStatus(RentalStatus status) {
        this.status = status;
    }
    

    public long getRentalDays() {
        return ChronoUnit.DAYS.between(rentalDate, returnDate) + 1;
    }
    
  
    public long getOverdueDays() {
        if (actualReturnDate != null) {
            return Math.max(0, ChronoUnit.DAYS.between(returnDate, actualReturnDate));
        } else {
            return Math.max(0, ChronoUnit.DAYS.between(returnDate, LocalDate.now()));
        }
    }
    
    public boolean isOverdue() {
        if (status == RentalStatus.RETURNED || status == RentalStatus.CANCELLED) {
            return false;
        }
        return LocalDate.now().isAfter(returnDate);
    }
    
    public void updateStatus() {
        if (status == RentalStatus.RETURNED || status == RentalStatus.CANCELLED) {
            return; 
        }
        
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(rentalDate)) {
            status = RentalStatus.RESERVED;
        } else if (today.isAfter(returnDate)) {
            status = RentalStatus.OVERDUE;
        } else {
            status = RentalStatus.ACTIVE;
        }
    }
    
    public double calculateLateFee() {
        long overdueDays = getOverdueDays();
        if (overdueDays <= 0) {
            return 0.0;
        }
        return overdueDays * this.dailyRate * 0.1; 
    }
    
    public double getTotalPayment() {
        if (status == RentalStatus.CANCELLED) {
            return 0.0;
        }
        return totalCost + lateFee;
    }
    
    public String getFormattedRentalDate() {
        return rentalDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
    
    public String getFormattedReturnDate() {
        return returnDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
    
    public String getFormattedActualReturnDate() {
        if (actualReturnDate == null) {
            return "Not returned";
        }
        return actualReturnDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
    
    public String toCsvString() {
        String actualReturnDateStr = (actualReturnDate != null) ? actualReturnDate.toString() : "";
        return String.join(",",
                rentalId,
                memberId,
                costumeId,
                size,
                rentalDate.toString(),
                returnDate.toString(),
                actualReturnDateStr,
                String.format("%.2f", totalCost),
                String.format("%.2f", dailyRate), 
                String.format("%.2f", lateFee),
                status.name()
        );
    }


    public static Rental fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        String rentalId = parts[0];
        String memberId = parts[1];
        String costumeId = parts[2];
        String size = parts[3];
        LocalDate rentalDate = LocalDate.parse(parts[4]);
        LocalDate returnDate = LocalDate.parse(parts[5]);
        LocalDate actualReturnDate = parts[6].isEmpty() ? null : LocalDate.parse(parts[6]);
        double totalCost = Double.parseDouble(parts[7]);
        
        double dailyRate;
        double lateFee;
        RentalStatus status;
        
        if (parts.length > 10) { 
            dailyRate = Double.parseDouble(parts[8]);
            lateFee = Double.parseDouble(parts[9]);
            status = RentalStatus.valueOf(parts[10]);
        } else { 
            lateFee = Double.parseDouble(parts[8]);
            status = RentalStatus.valueOf(parts[9]);
            long rentalDays = java.time.temporal.ChronoUnit.DAYS.between(rentalDate, returnDate) + 1;
            dailyRate = (rentalDays > 0) ? totalCost / rentalDays : totalCost;
        }

        return new Rental(rentalId, memberId, costumeId, size, rentalDate, returnDate, 
                          actualReturnDate, totalCost, dailyRate, lateFee, status);
    }
    
    @Override
    public String toString() {
        return String.format("Rental{id='%s', member='%s', costume='%s', period=%s to %s, status=%s, cost=%.2f}",
                rentalId, memberId, costumeId, getFormattedRentalDate(), getFormattedReturnDate(), 
                status.getDisplayName(), getTotalPayment());
    }
}