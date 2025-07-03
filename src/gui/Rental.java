package gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * レンタル情報を管理するクラス
 */
public class Rental {
    private String rentalId;          // レンタルID（R001, R002...）
    private String memberId;          // 会員ID
    private String costumeId;         // 衣装ID
    private String size;              // サイズ
    private LocalDate rentalDate;     // レンタル開始日
    private LocalDate returnDate;     // 返却予定日
    private LocalDate actualReturnDate; // 実際の返却日（null=未返却）
    private double totalCost;         // 総料金
    private double lateFee;           // 延滞金
    private RentalStatus status;      // レンタル状況
    
    // レンタル状況の列挙型
    public enum RentalStatus {
        ACTIVE("Active"),           // レンタル中
        RETURNED("Returned"),       // 返却済み
        OVERDUE("Overdue"),         // 延滞中
        CANCELLED("Cancelled");     // キャンセル
        
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
    
    // コンストラクタ（新規レンタル用）
    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, double totalCost) {
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.totalCost = totalCost;
        this.lateFee = 0.0;
        this.status = RentalStatus.ACTIVE;
        this.actualReturnDate = null;
    }
    
    // コンストラクタ（CSVから読み込み用）
    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, LocalDate actualReturnDate,
                  double totalCost, double lateFee, RentalStatus status) {
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCost = totalCost;
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
    
    // ユーティリティメソッド
    
    /**
     * レンタル期間（日数）を計算
     */
    public long getRentalDays() {
        return ChronoUnit.DAYS.between(rentalDate, returnDate) + 1;
    }
    
    /**
     * 延滞日数を計算
     */
    public long getOverdueDays() {
        if (actualReturnDate != null) {
            // 既に返却済みの場合
            return Math.max(0, ChronoUnit.DAYS.between(returnDate, actualReturnDate));
        } else {
            // まだ返却していない場合
            return Math.max(0, ChronoUnit.DAYS.between(returnDate, LocalDate.now()));
        }
    }
    
    /**
     * 延滞しているかどうかを判定
     */
    public boolean isOverdue() {
        if (status == RentalStatus.RETURNED || status == RentalStatus.CANCELLED) {
            return false;
        }
        return LocalDate.now().isAfter(returnDate);
    }
    
    /**
     * レンタル状況を自動更新
     */
    public void updateStatus() {
        if (status == RentalStatus.RETURNED || status == RentalStatus.CANCELLED) {
            return; // 既に完了している場合は更新しない
        }
        
        if (isOverdue()) {
            status = RentalStatus.OVERDUE;
        } else {
            status = RentalStatus.ACTIVE;
        }
    }
    
    /**
     * 延滞金を計算（1日あたり衣装料金の10%）
     */
    public double calculateLateFee(double dailyRate) {
        long overdueDays = getOverdueDays();
        if (overdueDays <= 0) {
            return 0.0;
        }
        return overdueDays * dailyRate * 0.1; // 1日あたり10%の延滞金
    }
    
    /**
     * 総支払い金額を計算（基本料金 + 延滞金）
     */
    public double getTotalPayment() {
        return totalCost + lateFee;
    }
    
    /**
     * 日付を文字列に変換（表示用）
     */
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
    
    /**
     * CSVファイル用の文字列形式に変換
     */
    public String toCsvString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String actualReturnStr = (actualReturnDate != null) ? actualReturnDate.format(formatter) : "";
        
        return String.join(",",
            rentalId,
            memberId,
            costumeId,
            size,
            rentalDate.format(formatter),
            returnDate.format(formatter),
            actualReturnStr,
            String.valueOf(totalCost),
            String.valueOf(lateFee),
            status.name()
        );
    }
    
    /**
     * CSV文字列からRentalオブジェクトを作成
     */
    public static Rental fromCsvString(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length != 10) {
            throw new IllegalArgumentException("Invalid CSV format for Rental");
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        String rentalId = parts[0].trim();
        String memberId = parts[1].trim();
        String costumeId = parts[2].trim();
        String size = parts[3].trim();
        LocalDate rentalDate = LocalDate.parse(parts[4].trim(), formatter);
        LocalDate returnDate = LocalDate.parse(parts[5].trim(), formatter);
        LocalDate actualReturnDate = parts[6].trim().isEmpty() ? null : LocalDate.parse(parts[6].trim(), formatter);
        double totalCost = Double.parseDouble(parts[7].trim());
        double lateFee = Double.parseDouble(parts[8].trim());
        RentalStatus status = RentalStatus.valueOf(parts[9].trim());
        
        return new Rental(rentalId, memberId, costumeId, size, rentalDate, returnDate, 
                         actualReturnDate, totalCost, lateFee, status);
    }
    
    @Override
    public String toString() {
        return String.format("Rental{id='%s', member='%s', costume='%s', period=%s to %s, status=%s, cost=%.2f}",
                rentalId, memberId, costumeId, getFormattedRentalDate(), getFormattedReturnDate(), 
                status.getDisplayName(), getTotalPayment());
    }
}