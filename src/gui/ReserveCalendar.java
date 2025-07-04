package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReserveCalendar extends JDialog {

    private String costumeId; // finalを削除して更新可能にする
    private String selectedSize; // finalを削除して更新可能にする
    private YearMonth currentMonth;
    private final JLabel monthLabel;
    private final JPanel calendarPanel;
    private final Map<LocalDate, Integer> reservationCounts = new HashMap<>();
    private int maxStock = 0;

    public ReserveCalendar(Frame owner, String costumeId, String selectedSize) {
        // 第3引数のモーダル設定を 'true' から 'false' に変更
        super(owner, "StockCalendar", false); 
        this.costumeId = costumeId;
        this.selectedSize = selectedSize;
        this.currentMonth = YearMonth.now();

        // 在庫と予約データをロード
        loadCostumeStock();
        loadReservations();

        // UIの初期化
        setLayout(new BorderLayout());
        // setSizeとsetLocationRelativeToはRentalFrame側で設定するため削除またはコメントアウト
        // setSize(600, 500);
        // setLocationRelativeTo(owner);

        // --- ここから追加 ---
        // 常に最前面に表示する設定
        setAlwaysOnTop(true);
        // --- ここまで追加 ---

        // ヘッダーパネル（月表示とナビゲーションボタン）
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        headerPanel.add(prevButton, BorderLayout.WEST);
        headerPanel.add(monthLabel, BorderLayout.CENTER);
        headerPanel.add(nextButton, BorderLayout.EAST);

        // カレンダーパネル
        calendarPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 曜日のヘッダーを追加
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            if (day.equals("Sun")) dayLabel.setForeground(Color.RED);
            if (day.equals("Sat")) dayLabel.setForeground(Color.BLUE);
            calendarPanel.add(dayLabel);
        }

        add(headerPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar();
    }

    /**
     * 新しい衣装IDとサイズでカレンダーの表示内容を更新するメソッド
     * @param newCostumeId 新しい衣装ID
     * @param newSelectedSize 新しいサイズ
     */
    public void updateData(String newCostumeId, String newSelectedSize) {
        this.costumeId = newCostumeId;
        this.selectedSize = newSelectedSize;
        
        // データを再読み込み
        reservationCounts.clear(); // 古い予約データをクリア
        loadCostumeStock();
        loadReservations();
        
        // カレンダー表示を更新
        updateCalendar();
    }

    private void loadCostumeStock() {
        String path = "gui/costumes.csv"; // パスを修正
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.trim().isEmpty()) continue;
                String[] values = line.split(",");
                if (values[0].equals(costumeId)) {
                    for (int i = 4; i < values.length - 1; i++) {
                        String[] stockInfo = values[i].split(":");
                        if (stockInfo[0].equals(selectedSize)) {
                            this.maxStock = Integer.parseInt(stockInfo[1]);
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // メッセージを英語に変更
            JOptionPane.showMessageDialog(this, "Failed to load costume data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadReservations() {
        String path = "gui/rentals.csv"; // パスを修正
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            // ヘッダー行を読み飛ばす
            while ((line = br.readLine()) != null && line.startsWith("#")) {
                // コメント行をスキップ
            }

            // 最初のデータ行から処理を開始
            do {
                if (line == null || line.trim().isEmpty()) continue;

                String[] values = line.split(",");
                // rentals.csvのフォーマット(10列)に合わせる
                if (values.length < 10) continue;

                String recordCostumeId = values[2];
                String recordSize = values[3];
                String status = values[9];

                // 対象の衣装・サイズで、かつステータスが"ACTIVE"（レンタル中/予約中）のものだけを対象とする
                if (recordCostumeId.equals(costumeId) && recordSize.equals(selectedSize) && status.equals("ACTIVE")) {
                    try {
                        LocalDate startDate = LocalDate.parse(values[4]);
                        LocalDate endDate = LocalDate.parse(values[5]);

                        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                            reservationCounts.put(date, reservationCounts.getOrDefault(date, 0) + 1);
                        }
                    } catch (java.time.format.DateTimeParseException e) {
                        System.err.println("Failed to parse date format: " + line);
                        // Skip lines with parsing errors
                    }
                }
            } while ((line = br.readLine()) != null);

        } catch (IOException e) {
            e.printStackTrace();
            // メッセージを英語に変更
            JOptionPane.showMessageDialog(this, "Failed to load rental data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCalendar() {
        // 月のラベルを更新 (英語表記に変更)
        monthLabel.setText(currentMonth.format(DateTimeFormatter.ofPattern("M yyyy")));
        
        // カレンダーパネルをクリア（曜日のヘッダーは残す）
        while (calendarPanel.getComponentCount() > 7) {
            calendarPanel.remove(7);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeekOfFirst = firstDayOfMonth.getDayOfWeek().getValue() % 7; // 日曜日を0とする

        // 月の始まる前の空白を追加
        for (int i = 0; i < dayOfWeekOfFirst; i++) {
            calendarPanel.add(new JLabel(""));
        }

        // 日付と在庫を追加
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now(); // 今日の日付を取得

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentMonth.atDay(day);
            int reservedCount = reservationCounts.getOrDefault(currentDate, 0);
            int availableStock = maxStock - reservedCount;

            JPanel dayPanel = new JPanel(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.LEFT);
            dayLabel.setBorder(new EmptyBorder(2, 5, 2, 2));
            
            JLabel stockLabel = new JLabel(String.valueOf(availableStock), SwingConstants.CENTER);
            stockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            // 過去の日付かどうかをチェック
            if (currentDate.isBefore(today)) {
                dayLabel.setForeground(Color.GRAY);
                stockLabel.setForeground(Color.GRAY);
                stockLabel.setText("-"); // 在庫数の代わりにハイフンを表示
            } else {
                // 今日以降の日付の場合、在庫数に応じて色を変更
                if (availableStock <= 0) {
                    stockLabel.setForeground(Color.RED);
                    stockLabel.setText("stock: 0");
                } else if (availableStock < 3) {
                    stockLabel.setForeground(new Color(255, 165, 0)); // オレンジ
                } else {
                    stockLabel.setForeground(Color.BLUE);
                }
            }

            dayPanel.add(dayLabel, BorderLayout.NORTH);
            dayPanel.add(stockLabel, BorderLayout.CENTER);
            calendarPanel.add(dayPanel);
        }

        revalidate();
        repaint();
    }
}
