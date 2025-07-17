package gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileIO {
    private static final Path REGISTRATER_PATH = Paths.get("./gui/Registrater.csv");
    private static final Path COSTUMES_PATH = Paths.get("./gui/costumes.csv");
    private static final Path RENTALS_PATH = Paths.get("./gui/rentals.csv");
    
    private static FileIO instance;

    // Private constructor for Singleton
    private FileIO() {
        initializeAllFiles();
    }

    // Singleton pattern
    public static synchronized FileIO getInstance() {
        if (instance == null) {
            instance = new FileIO();
        }
        return instance;
    }
    
    private void initializeAllFiles() {
        initializeSingleFile(REGISTRATER_PATH);
        initializeSingleFile(COSTUMES_PATH);
        initializeSingleFile(RENTALS_PATH);
    }

    private void initializeSingleFile(Path path) {
        try {
            if (!Files.exists(path)) {
                Path parentDir = path.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                Files.createFile(path);
                System.out.println("Created file: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error initializing file " + path + ": " + e.getMessage());
            throw new RuntimeException("Failed to initialize file: " + path, e);
        }
    }
    
    // Generic method to read all lines from any file
    public List<String> readAllLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // Generic method to write all lines to any file
    public boolean writeAllLines(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines, StandardCharsets.UTF_8, 
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + filePath + " - " + e.getMessage());
            return false;
        }
    }



    public boolean write(String name, String memberId, String email, String phone, String password, String address) {
        if (isMemberIdExists(memberId)) {
            System.err.println("Cannot write member data: Member ID already exists");
            return false;
        }

        String registrationDate = LocalDate.now().toString();
        String line = String.join(",", escape(name), memberId, escape(email), phone, password, escape(address), registrationDate);
        try {
            Files.write(REGISTRATER_PATH, Collections.singletonList(line), StandardCharsets.UTF_8,
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to registrations file: " + e.getMessage());
            return false;
        }
    }

    
    public boolean isMemberIdExists(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) return false;
        List<String> lines = readAllLines(REGISTRATER_PATH.toString());
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 2 && parts[1].trim().equals(memberId.trim())) {
                return true;
            }
        }
        return false;
    }
    
 
    public boolean isValidLogin(String memberId, String password) {
         if (memberId == null || password == null || memberId.trim().isEmpty() || password.isEmpty()) {
            return false;
        }
        List<String> lines = readAllLines(REGISTRATER_PATH.toString());
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[1].trim().equals(memberId.trim()) && parts[4].equals(password)) {
                return true;
            }
        }
        return false;
    }

    // MemberData class remains the same
    public static class MemberData {
        private final String name, memberId, email, phone, password, address, registrationDate;
        public MemberData(String name, String memberId, String email, String phone, String password, String address, String registrationDate) {
            this.name = name; this.memberId = memberId; this.email = email;
            this.phone = phone; this.password = password; this.address = address;
            this.registrationDate = registrationDate;
        }
        public String getName() { return name; }
        public String getMemberId() { return memberId; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getPassword() { return password; }
        public String getAddress() { return address; }
        public String getRegistrationDate() { return registrationDate; }
    }

  
    public List<MemberData> getAllMembers() {
        List<MemberData> members = new ArrayList<>();
        List<String> lines = readAllLines(REGISTRATER_PATH.toString());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                String registrationDate = (parts.length > 6) ? unescape(parts[6]) : "N/A";
                members.add(new MemberData(
                    unescape(parts[0]), parts[1].trim(), unescape(parts[2]), 
                    parts[3], parts[4], unescape(parts[5]), registrationDate));
            }
        }
        return members;
    }
    

    public MemberData getMemberData(String memberId) {
        return getAllMembers().stream()
            .filter(m -> m.getMemberId().equals(memberId))
            .findFirst().orElse(null);
    }
    
    // Update an existing member's information
    public boolean updateMember(String memberId, MemberData updatedData) {
        List<MemberData> members = getAllMembers();
        boolean memberFound = false;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getMemberId().equals(memberId)) {
                members.set(i, updatedData);
                memberFound = true;
                break;
            }
        }
        if (!memberFound) return false;
        
        List<String> lines = new ArrayList<>();
        for (MemberData member : members) {
            lines.add(String.join(",", escape(member.getName()), member.getMemberId(), escape(member.getEmail()),
                member.getPhone(), member.getPassword(), escape(member.getAddress()), escape(member.getRegistrationDate())));
        }
        return writeAllLines(REGISTRATER_PATH.toString(), lines);
    }

    // Delete a member by ID
    public boolean deleteMember(String memberId) {
        List<MemberData> members = getAllMembers();
        boolean removed = members.removeIf(m -> m.getMemberId().equals(memberId));
        if (!removed) return false;

        List<String> lines = new ArrayList<>();
        for (MemberData member : members) {
            lines.add(String.join(",", escape(member.getName()), member.getMemberId(), escape(member.getEmail()),
                member.getPhone(), member.getPassword(), escape(member.getAddress()), escape(member.getRegistrationDate())));
        }
        return writeAllLines(REGISTRATER_PATH.toString(), lines);
    }

    // Get total number of registered members
    public int getRegistrationCount() {
        return (int) readAllLines(REGISTRATER_PATH.toString()).stream()
            .filter(line -> !line.trim().isEmpty()).count();
    }
    
    // Get total number of unique costumes
    public int getAvailableCostumesCount() {
        return (int) readAllLines(COSTUMES_PATH.toString()).stream()
            .filter(line -> !line.trim().isEmpty() && !line.startsWith("#")).count();
    }

    // Get count of currently active rentals
    public int getActiveRentalsCount() {
        List<String> lines = readAllLines(RENTALS_PATH.toString());
        int count = 0;
        for (String line : lines) {
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            String[] values = line.split(",");
            if (values.length >= 11) {
                String status = values[10].trim();
                if ("ACTIVE".equals(status) || "OVERDUE".equals(status)) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // check for stock availability
    public boolean isStockAvailableForPeriod(String costumeId, String size, LocalDate startDate, LocalDate endDate) {
        int maxStock = getCostumeStock(costumeId, size);
        if (maxStock <= 0) return false;

        Map<LocalDate, Integer> reservations = getReservationCounts(costumeId, size);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (reservations.getOrDefault(date, 0) >= maxStock) {
                return false; // Stock is not available on this day
            }
        }
        return true; // Stock is available for the entire period
    }
    
    public int getCostumeStock(String costumeId, String size) {
         List<String> lines = readAllLines(COSTUMES_PATH.toString());
         for (String line : lines) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            String[] values = line.split(",");
            if (values.length > 4 && values[0].trim().equals(costumeId)) {
                for (int i = 4; i < values.length -1; i++) {
                    String[] stockInfo = values[i].split(":");
                    if (stockInfo.length == 2 && stockInfo[0].trim().equals(size)) {
                        return Integer.parseInt(stockInfo[1].trim());
                    }
                }
            }
         }
         return 0;
    }

    public Map<LocalDate, Integer> getReservationCounts(String costumeId, String size) {
        Map<LocalDate, Integer> counts = new HashMap<>();
        List<String> lines = readAllLines(RENTALS_PATH.toString());
        for (String line : lines) {
            if (line.startsWith("#") || line.trim().isEmpty()) continue;
            String[] values = line.split(",");
            if (values.length >= 11) {
                String recordCostumeId = values[2].trim();
                String recordSize = values[3].trim();
                String status = values[10].trim();
                if (recordCostumeId.equals(costumeId) && recordSize.equals(size) &&
                    !"CANCELLED".equals(status) && !"RETURNED".equals(status)) {
                    try {
                        LocalDate rentalDate = LocalDate.parse(values[4].trim());
                        LocalDate returnDate = LocalDate.parse(values[5].trim());
                        for (LocalDate date = rentalDate; !date.isAfter(returnDate); date = date.plusDays(1)) {
                            counts.put(date, counts.getOrDefault(date, 0) + 1);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to parse date in line: " + line);
                    }
                }
            }
        }
        return counts;
    }
    
    // Utility methods for handling commas in CSV data
    private String escape(String data) {
        if (data == null) {
            return "";
        }
        if (data.contains(",")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }

    private String unescape(String data) {
        if (data.startsWith("\"") && data.endsWith("\"")) {
            return data.substring(1, data.length() - 1).replace("\"\"", "\"");
        }
        return data;
    }
}