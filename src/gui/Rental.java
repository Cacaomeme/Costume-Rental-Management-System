package gui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * レンタル情報を管理するクラス
 */
public class Rental {
    // レンタル状況の列挙型
    public enum RentalStatus {
        RESERVED("Reserved"),       // 予約済み（レンタル前） ★ 追加
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
    
    private String rentalId;
    private String memberId;
    private String costumeId;
    private String size;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private double totalCost;
    private double dailyRate; // ★ 追加
    private double lateFee;
    private RentalStatus status;

    // コンストラクタ（新規レンタル用）
    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, double totalCost, double dailyRate) { // ★ dailyRate を追加
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.totalCost = totalCost;
        this.dailyRate = dailyRate; // ★ 追加
        this.lateFee = 0.0;
        this.status = RentalStatus.RESERVED; // ★ 初期ステータスをRESERVEDに変更
        this.actualReturnDate = null;
        updateStatus(); // 初期ステータスを更新
    }
    
    // コンストラクタ（CSVから読み込み用）
    public Rental(String rentalId, String memberId, String costumeId, String size,
                  LocalDate rentalDate, LocalDate returnDate, LocalDate actualReturnDate,
                  double totalCost, double dailyRate, double lateFee, RentalStatus status) { // ★ dailyRate を追加
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.costumeId = costumeId;
        this.size = size;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.actualReturnDate = actualReturnDate;
        this.totalCost = totalCost;
        this.dailyRate = dailyRate; // ★ 追加
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
    
    public double getDailyRate() { // ★ 追加
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
        
        LocalDate today = LocalDate.now();
        
        // ★ レンタル開始前か、期間中か、延滞中かを判定
        if (today.isBefore(rentalDate)) {
            status = RentalStatus.RESERVED;
        } else if (today.isAfter(returnDate)) {
            status = RentalStatus.OVERDUE;
        } else {
            status = RentalStatus.ACTIVE;
        }
    }
    
    /**
     * 延滞金を計算（フィールドのdailyRateを使用）
     */
    public double calculateLateFee() { // ★ 引数を削除
        long overdueDays = getOverdueDays();
        if (overdueDays <= 0) {
            return 0.0;
        }
        return overdueDays * this.dailyRate * 0.1; // 1日あたり10%の延滞金
    }
    
    /**
     * 総支払い金額を計算（基本料金 + 延滞金）
     * キャンセルされたレンタルの場合は0を返す
     */
    public double getTotalPayment() {
        // キャンセルされたレンタルは費用が発生しない
        if (status == RentalStatus.CANCELLED) {
            return 0.0;
        }
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
                String.format("%.2f", dailyRate), // ★ 追加
                String.format("%.2f", lateFee),
                status.name()
        );
    }

    /**
     * CSV文字列からRentalオブジェクトを作成
     */
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
        
        // ★ dailyRateの読み込み（古いフォーマットにも対応）
        double dailyRate;
        double lateFee;
        RentalStatus status;
        
        if (parts.length > 10) { // 新しいフォーマット (11列)
            dailyRate = Double.parseDouble(parts[8]);
            lateFee = Double.parseDouble(parts[9]);
            status = RentalStatus.valueOf(parts[10]);
        } else { // 古いフォーマット (10列)
            lateFee = Double.parseDouble(parts[8]);
            status = RentalStatus.valueOf(parts[9]);
            // dailyRateを計算して補完
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