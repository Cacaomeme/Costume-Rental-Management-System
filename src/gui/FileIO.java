package gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced FileIO class for comprehensive member data management
 * Supports CRUD operations for member information
 */
public class FileIO {
    private Path registraterPath;
    private Path costumesPath;
    private Path rentalsPath;
    private static FileIO instance;

    public FileIO() {
        // ファイルパスの初期化
        this.registraterPath = Paths.get("gui/Registrater.csv");
        this.costumesPath = Paths.get("gui/costumes.csv");
        this.rentalsPath = Paths.get("gui/rentals.csv");
        initializeFile();
    }

    // Singletonパターン（オプション）
    public static FileIO getInstance() {
        if (instance == null) {
            instance = new FileIO();
        }
        return instance;
    }

    /**
     * 登録ファイルの初期化
     */
    private void initializeFile() {
        try {
            // 親ディレクトリが存在しない場合は作成
            Path parentDir = registraterPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            // ファイルが存在しない場合は作成
            if (!Files.exists(registraterPath)) {
                Files.createFile(registraterPath);
                System.out.println("Created registration file: " + registraterPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating registrations file: " + e.getMessage());
            throw new RuntimeException("Failed to initialize registration file", e);
        }
    }

    /**
     * 新規会員登録データの書き込み
     */
    public boolean Write(String name, String memberId, String email, String phone, String password, String address) {
        // 入力値の検証
        if (name == null || memberId == null || email == null || phone == null || 
            password == null || address == null) {
            System.err.println("Cannot write member data: null values not allowed");
            return false;
        }

        // 重複会員IDのチェック
        if (isMemberIdExists(memberId)) {
            System.err.println("Cannot write member data: Member ID already exists");
            return false;
        }

        try {
            // CSVでカンマが含まれる場合のエスケープ処理
            String escapedName = escapeCommas(name);
            String escapedEmail = escapeCommas(email);
            String escapedAddress = escapeCommas(address);
            
            String line = String.join(",", 
                escapedName, memberId, escapedEmail, phone, password, escapedAddress);
            
            List<String> lines = new ArrayList<>();
            lines.add(line);
            
            Files.write(registraterPath, lines, StandardCharsets.UTF_8,
                       StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            
            System.out.println("Successfully registered member: " + memberId);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error writing to registrations file: " + e.getMessage());
            return false;
        }
    }

    /**
     * 会員IDの存在チェック
     */
    public boolean isMemberIdExists(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(memberId.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return false;
    }

    /**
     * 指定された衣装とサイズの最大在庫数を取得します。
     * @param costumeId 衣装ID
     * @param size サイズ
     * @return 在庫数。見つからない場合は0。
     */
    public int getCostumeStock(String costumeId, String size) {
        try {
            List<String> lines = Files.readAllLines(costumesPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length > 4 && values[0].equals(costumeId)) {
                    // サイズと在庫の情報は4番目の要素から始まる
                    for (int i = 4; i < values.length - 1; i++) {
                        String[] stockInfo = values[i].split(":");
                        if (stockInfo.length == 2 && stockInfo[0].equals(size)) {
                            return Integer.parseInt(stockInfo[1]);
                        }
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading costume stock from " + costumesPath + ": " + e.getMessage());
        }
        return 0; // 見つからなかった場合
    }

    /**
     * 指定された衣装とサイズの予約状況を取得します。
     * @param costumeId 衣装ID
     * @param size サイズ
     * @return 日付ごとの予約数を格納したMap
     */
    public Map<LocalDate, Integer> getReservationCounts(String costumeId, String size) {
        Map<LocalDate, Integer> counts = new HashMap<>();
        
        try {
            if (!Files.exists(rentalsPath)) {
                return counts;
            }

            List<String> lines = Files.readAllLines(rentalsPath, StandardCharsets.UTF_8);
            
            for (String line : lines) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                
                // レンタルファイルの形式: rentalId,memberId,costumeId,size,rentalDate,returnDate,actualReturnDate,totalCost,dailyRate,lateFee,status
                if (values.length >= 11) {
                    try {
                        String recordCostumeId = values[2].trim();
                        String recordSize = values[3].trim();
                        String rentalDateStr = values[4].trim();
                        String returnDateStr = values[5].trim();
                        String status = values[10].trim();

                        // 指定された衣装とサイズ、かつアクティブなレンタルのみ対象
                        if (recordCostumeId.equals(costumeId) && recordSize.equals(size)) {
                            // CANCELLEDは除外し、その他のステータスは予約として扱う
                            if (!"CANCELLED".equals(status)) {
                                try {
                                    LocalDate rentalDate = LocalDate.parse(rentalDateStr);
                                    LocalDate returnDate = LocalDate.parse(returnDateStr);
                                    int quantity = 1; // 各レンタル記録は1つの衣装として扱う

                                    // レンタル期間中の各日付で予約数をカウント
                                    for (LocalDate date = rentalDate; !date.isAfter(returnDate); date = date.plusDays(1)) {
                                        counts.put(date, counts.getOrDefault(date, 0) + quantity);
                                    }
                                } catch (java.time.format.DateTimeParseException e) {
                                    System.err.println("Failed to parse date in line: " + line);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing rental line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading rental data from " + rentalsPath + ": " + e.getMessage());
        }
        
        return counts;
    }

    /**
     * 指定された期間中、衣装の在庫が利用可能かチェックします。
     * @param costumeId 衣装ID
     * @param size サイズ
     * @param startDate レンタル開始日
     * @param endDate レンタル終了日
     * @return 期間中すべての日で在庫があればtrue、1日でも在庫がなければfalse
     */
    public boolean isStockAvailableForPeriod(String costumeId, String size, LocalDate startDate, LocalDate endDate) {
        int maxStock = getCostumeStock(costumeId, size);
        if (maxStock <= 0) {
            return false;
        }
        Map<LocalDate, Integer> reservationCounts = getReservationCounts(costumeId, size);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int reservedCount = reservationCounts.getOrDefault(date, 0);
            if (maxStock - reservedCount <= 0) {
                return false; // この日の在庫がない
            }
        }
        return true; // 期間中すべての日に在庫がある
    }

    /**
     * アクティブなレンタル数を取得します
     * @return アクティブなレンタル数
     */
    public int getActiveRentalsCount() {
        try {
            List<String> lines = Files.readAllLines(rentalsPath, StandardCharsets.UTF_8);
            int count = 0;
            
            for (String line : lines) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length >= 10) {
                    String status = values[9].trim();
                    if ("ACTIVE".equals(status)) {
                        count++;
                    }
                }
            }
            return count;
        } catch (IOException e) {
            System.err.println("Error reading rentals file: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 利用可能なコスチューム数（種類数）を取得します
     * @return 利用可能なコスチューム数
     */
    public int getAvailableCostumesCount() {
        try {
            List<String> lines = Files.readAllLines(costumesPath, StandardCharsets.UTF_8);
            int count = 0;
            
            for (String line : lines) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                
                String[] values = line.split(",");
                if (values.length > 4) {
                    // コスチュームの行をカウント
                    count++;
                }
            }
            return count;
        } catch (IOException e) {
            System.err.println("Error reading costumes file: " + e.getMessage());
            return 0;
        }
    }

    /**
     * ログイン認証
     */
    public boolean isValidLogin(String memberId, String password) {
        if (memberId == null || password == null || 
            memberId.trim().isEmpty() || password.isEmpty()) {
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6 && 
                    parts[1].trim().equals(memberId.trim()) && 
                    parts[4].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return false;
    }


    /**
     * 会員IDで会員データを取得
     */
    public MemberData getMemberData(String memberId) {
        if (memberId == null || memberId.trim().isEmpty()) {
            return null;
        }

        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(memberId.trim())) {
                    return new MemberData(
                        unescapeCommas(parts[0]), // name
                        parts[1].trim(),          // memberId
                        unescapeCommas(parts[2]), // email
                        parts[3],                 // phone
                        parts[4],                 // password
                        unescapeCommas(parts[5])  // address
                    );
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return null;
    }

    /**
     * 会員情報の更新（パスワード以外）
     */
    public boolean updateMemberInfo(String memberId, String newName, String newEmail, String newPhone, String newAddress) {
        if (memberId == null || newName == null || newEmail == null || newPhone == null || newAddress == null) {
            System.err.println("Cannot update member info: null values not allowed");
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            boolean memberFound = false;
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(memberId.trim())) {
                    // 会員が見つかった場合、新しい情報で行を更新
                    String escapedName = escapeCommas(newName);
                    String escapedEmail = escapeCommas(newEmail);
                    String escapedAddress = escapeCommas(newAddress);
                    
                    String updatedLine = String.join(",",
                        escapedName,              // 更新された名前
                        parts[1],                 // 会員ID（変更なし）
                        escapedEmail,             // 更新されたメール
                        newPhone,                 // 更新された電話番号
                        parts[4],                 // パスワード（変更なし）
                        escapedAddress            // 更新された住所
                    );
                    
                    lines.set(i, updatedLine);
                    memberFound = true;
                    break;
                }
            }
            
            if (!memberFound) {
                System.err.println("Member not found: " + memberId);
                return false;
            }
            
            // ファイルに全行を書き戻し
            Files.write(registraterPath, lines, StandardCharsets.UTF_8,
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Successfully updated member info: " + memberId);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error updating member info: " + e.getMessage());
            return false;
        }
    }

    /**
     * パスワードの変更
     */
    public boolean changePassword(String memberId, String newPassword) {
        if (memberId == null || newPassword == null || 
            memberId.trim().isEmpty() || newPassword.isEmpty()) {
            System.err.println("Cannot change password: invalid parameters");
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            boolean memberFound = false;
            
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[1].trim().equals(memberId.trim())) {
                    // 会員が見つかった場合、パスワードを更新
                    String updatedLine = String.join(",",
                        parts[0],                 // 名前（変更なし）
                        parts[1],                 // 会員ID（変更なし）
                        parts[2],                 // メール（変更なし）
                        parts[3],                 // 電話番号（変更なし）
                        newPassword,              // 新しいパスワード
                        parts[5]                  // 住所（変更なし）
                    );
                    
                    lines.set(i, updatedLine);
                    memberFound = true;
                    break;
                }
            }
            
            if (!memberFound) {
                System.err.println("Member not found: " + memberId);
                return false;
            }
            
            // ファイルに全行を書き戻し
            Files.write(registraterPath, lines, StandardCharsets.UTF_8,
                       StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            System.out.println("Successfully changed password for member: " + memberId);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    /**
     * 全会員データの取得
     */
    public List<MemberData> getAllMembers() {
        List<MemberData> members = new ArrayList<>();
        
        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    members.add(new MemberData(
                        unescapeCommas(parts[0]), // name
                        parts[1].trim(),          // memberId
                        unescapeCommas(parts[2]), // email
                        parts[3],                 // phone
                        parts[4],                 // password
                        unescapeCommas(parts[5])  // address
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
        }
        return members;
    }

    /**
     * 登録数の取得
     */
    public int getRegistrationCount() {
        try {
            List<String> lines = Files.readAllLines(registraterPath, StandardCharsets.UTF_8);
            return (int) lines.stream().filter(line -> !line.trim().isEmpty()).count();
        } catch (IOException e) {
            System.err.println("Error reading registrations file: " + e.getMessage());
            return 0;
        }
    }

    /**
     * データのバックアップ
     */
    public boolean backupData() {
        try {
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            Path backupPath = Paths.get("gui/backup_" + timestamp + "_Registrater.txt");
            Files.copy(registraterPath, backupPath);
            
            System.out.println("Backup created: " + backupPath.toAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * CSVでカンマをエスケープ
     */
    private String escapeCommas(String data) {
        if (data.contains(",")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }

    /**
     * エスケープされたカンマを元に戻す
     */
    private String unescapeCommas(String data) {
        if (data.startsWith("\"") && data.endsWith("\"")) {
            return data.substring(1, data.length() - 1).replace("\"\"", "\"");
        }
        return data;
    }

    /**
     * 会員データを表すクラス
     */
    public static class MemberData {
        private final String name;
        private final String memberId;
        private final String email;
        private final String phone;
        private final String password;
        private final String address;

        public MemberData(String name, String memberId, String email, String phone, String password, String address) {
            this.name = name;
            this.memberId = memberId;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.address = address;
        }

        // Getters
        public String getName() { return name; }
        public String getMemberId() { return memberId; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getPassword() { return password; }
        public String getAddress() { return address; }

        @Override
        public String toString() {
            return "MemberData{" +
                    "name='" + name + '\'' +
                    ", memberId='" + memberId + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }
}
