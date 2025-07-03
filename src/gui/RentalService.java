package gui;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * レンタル情報の管理サービスクラス
 */
public class RentalService {
    private static final String RENTAL_FILE_PATH = "gui/rentals.csv";
    private List<Rental> allRentals;
    private CostumeDataManager costumeManager;
    
    public RentalService() {
        this.allRentals = new ArrayList<>();
        this.costumeManager = new CostumeDataManager();
        loadRentals();
    }
    
    /**
     * レンタル情報をCSVファイルから読み込み
     */
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
                // コメント行や空行をスキップ
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                try {
                    Rental rental = Rental.fromCsvString(line);
                    rental.updateStatus(); // 状況を最新に更新
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
    
    /**
     * 空のレンタルファイルを作成
     */
    private void createEmptyRentalFile() {
        try {
            File file = new File(RENTAL_FILE_PATH);
            file.getParentFile().mkdirs(); // ディレクトリを作成
            
            try (PrintWriter writer = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                writer.println("# Rental Management System - Rental Records");
                writer.println("# Format: rentalId,memberId,costumeId,size,rentalDate,returnDate,actualReturnDate,totalCost,lateFee,status");
            }
            
        } catch (IOException e) {
            System.err.println("Error creating rental file: " + e.getMessage());
        }
    }
    
    /**
     * レンタル情報をCSVファイルに保存
     */
    public void saveRentals() {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(RENTAL_FILE_PATH), StandardCharsets.UTF_8))) {
            
            writer.println("# Rental Management System - Rental Records");
            writer.println("# Format: rentalId,memberId,costumeId,size,rentalDate,returnDate,actualReturnDate,totalCost,lateFee,status");
            
            for (Rental rental : allRentals) {
                writer.println(rental.toCsvString());
            }
            
            System.out.println("Saved " + allRentals.size() + " rentals to file");
            
        } catch (IOException e) {
            System.err.println("Error saving rental file: " + e.getMessage());
            throw new RuntimeException("Failed to save rental data", e);
        }
    }
    
    /**
     * 新規レンタルIDを生成
     */
    public String generateNewRentalId() {
        int maxId = 0;
        for (Rental rental : allRentals) {
            String id = rental.getRentalId();
            if (id.startsWith("R") && id.length() > 1) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    maxId = Math.max(maxId, num);
                } catch (NumberFormatException e) {
                    // IDの形式が異なる場合はスキップ
                }
            }
        }
        return String.format("R%03d", maxId + 1);
    }
    
    /**
     * 新しいレンタルを作成
     */
    public boolean createRental(String memberId, String costumeId, String size, LocalDate rentalDate, 
                               LocalDate returnDate, double totalCost) {
        try {
            // 衣装の在庫確認
            if (!isCostumeAvailable(costumeId, size)) {
                System.err.println("Costume " + costumeId + " size " + size + " is not available for rental");
                return false;
            }
            
            String rentalId = generateNewRentalId();
            Rental newRental = new Rental(rentalId, memberId, costumeId, size, rentalDate, returnDate, totalCost);
            
            allRentals.add(newRental);
            
            // 衣装の在庫を減らす
            updateCostumeStock(costumeId, size, -1);
            
            // ファイルに保存
            saveRentals();
            
            System.out.println("Created new rental: " + newRental);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating rental: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 衣装が利用可能かチェック
     */
    public boolean isCostumeAvailable(String costumeId) {
        List<Costume> costumes = costumeManager.loadCostumes();
        for (Costume costume : costumes) {
            if (costume.getCostumeId().equals(costumeId)) {
                return costume.getTotalStock() > 0;
            }
        }
        return false;
    }
    
    /**
     * 特定サイズの衣装が利用可能かチェック
     */
    public boolean isCostumeAvailable(String costumeId, String size) {
        List<Costume> costumes = costumeManager.loadCostumes();
        for (Costume costume : costumes) {
            if (costume.getCostumeId().equals(costumeId)) {
                return costume.hasSize(size);
            }
        }
        return false;
    }
    
    /**
     * 衣装の在庫を更新（レンタル時-1、返却時+1）
     */
    private void updateCostumeStock(String costumeId, int change) {
        // TODO: CostumeDataManagerに在庫更新機能を追加する必要がある
        System.out.println("Stock update for costume " + costumeId + ": " + change);
        // 現在はログ出力のみ。実際の実装では衣装データファイルを更新する
    }
    
    /**
     * 特定サイズの衣装の在庫を更新（レンタル時-1、返却時+1）
     */
    private void updateCostumeStock(String costumeId, String size, int change) {
        // TODO: CostumeDataManagerに在庫更新機能を追加する必要がある
        System.out.println("Stock update for costume " + costumeId + " size " + size + ": " + change);
        // 現在はログ出力のみ。実際の実装では衣装データファイルを更新する
    }
    
    /**
     * 特定会員のレンタル履歴を取得
     */
    public List<Rental> getRentalsByMemberId(String memberId) {
        return allRentals.stream()
                .filter(rental -> rental.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }
    
    /**
     * 特定会員のアクティブなレンタルを取得
     */
    public List<Rental> getActiveRentalsByMemberId(String memberId) {
        return allRentals.stream()
                .filter(rental -> rental.getMemberId().equals(memberId))
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.ACTIVE || 
                                rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    /**
     * 特定衣装のアクティブなレンタルを取得
     */
    public List<Rental> getActiveRentalsByCostumeId(String costumeId) {
        return allRentals.stream()
                .filter(rental -> rental.getCostumeId().equals(costumeId))
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.ACTIVE || 
                                rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    /**
     * 延滞中のレンタルを取得
     */
    public List<Rental> getOverdueRentals() {
        return allRentals.stream()
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .collect(Collectors.toList());
    }
    
    /**
     * レンタルを返却処理
     */
    public boolean returnRental(String rentalId, LocalDate actualReturnDate) {
        for (Rental rental : allRentals) {
            if (rental.getRentalId().equals(rentalId)) {
                if (rental.getStatus() == Rental.RentalStatus.RETURNED) {
                    System.err.println("Rental " + rentalId + " is already returned");
                    return false;
                }
                
                rental.setActualReturnDate(actualReturnDate);
                rental.setStatus(Rental.RentalStatus.RETURNED);
                
                // 延滞金計算
                if (rental.getOverdueDays() > 0) {
                    // TODO: 衣装の日額料金を取得して延滞金を計算
                    double dailyRate = rental.getTotalCost() / rental.getRentalDays();
                    double lateFee = rental.calculateLateFee(dailyRate);
                    rental.setLateFee(lateFee);
                }
                
                // 衣装の在庫を戻す
                updateCostumeStock(rental.getCostumeId(), rental.getSize(), 1);
                
                // ファイルに保存
                saveRentals();
                
                System.out.println("Returned rental: " + rental);
                return true;
            }
        }
        
        System.err.println("Rental " + rentalId + " not found");
        return false;
    }
    
    /**
     * レンタル状況を更新（延滞チェック等）
     */
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
    
    /**
     * レンタル期間の日数計算
     */
    public static long calculateRentalDays(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * レンタル料金計算（基本料金 × 日数）
     */
    public static double calculateRentalCost(double dailyRate, long days) {
        return dailyRate * days;
    }
    
    /**
     * 全レンタル情報を取得
     */
    public List<Rental> getAllRentals() {
        return new ArrayList<>(allRentals);
    }
    
    /**
     * レンタル情報を検索
     */
    public Rental findRentalById(String rentalId) {
        return allRentals.stream()
                .filter(rental -> rental.getRentalId().equals(rentalId))
                .findFirst()
                .orElse(null);
    }
}