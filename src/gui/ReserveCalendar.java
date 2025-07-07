package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ReserveCalendar extends JDialog {

    private String costumeId;
    private String selectedSize;
    private YearMonth currentMonth;
    private final JLabel monthLabel;
    private final JPanel calendarPanel;
    private final Map<LocalDate, Integer> reservationCounts = new HashMap<>();
    private int maxStock = 0;
    private final FileIO fileIO; // FileIOインスタンスを追加

    public ReserveCalendar(Frame owner, String costumeId, String selectedSize) {
        super(owner, "StockCalendar", false); 
        this.costumeId = costumeId;
        this.selectedSize = selectedSize;
        this.currentMonth = YearMonth.now();
        this.fileIO = FileIO.getInstance(); // FileIOインスタンスを取得

        // 在庫と予約データをロード
        loadCostumeStock();
        loadReservations();

        // UIの初期化
        setLayout(new BorderLayout());

        setAlwaysOnTop(true);

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
        
        // ★ データを再読み込み
        loadCostumeStock();
        loadReservations();
        
        // ★ カレンダー表示を更新
        updateCalendar();
    }

    private void loadCostumeStock() {
        // FileIOから最新の在庫数を取得
        this.maxStock = fileIO.getCostumeStock(this.costumeId, this.selectedSize);
    }

    private void loadReservations() {
        // FileIOから最新の予約状況を取得
        this.reservationCounts.clear();
        Map<LocalDate, Integer> newCounts = fileIO.getReservationCounts(this.costumeId, this.selectedSize);
        this.reservationCounts.putAll(newCounts);
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
                    stockLabel.setText("0");
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