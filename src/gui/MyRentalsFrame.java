package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * レンタル履歴表示画面
 */
public class MyRentalsFrame extends JFrame {
    private String currentMemberId;
    private RentalService rentalService;
    private CostumeDataManager costumeManager;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable activeRentalsTable;
    private JTable historyTable;
    private DefaultTableModel activeTableModel;
    private DefaultTableModel historyTableModel;
    
    private JLabel totalRentalsLabel;
    private JLabel totalCostLabel;
    private JLabel overdueCountLabel;
    
    // データ
    private List<Rental> allRentals;
    private List<Rental> activeRentals;
    private List<Rental> rentalHistory;
    private List<Costume> allCostumes;

    public MyRentalsFrame(String memberId) {
        this.currentMemberId = memberId;
        this.rentalService = new RentalService();
        this.costumeManager = new CostumeDataManager();
        
        loadData();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadRentalData();
        setupFrame();
    }
    
    private void loadData() {
        // レンタル状況を最新に更新
        rentalService.updateAllRentalStatuses();
        
        allRentals = rentalService.getRentalsByMemberId(currentMemberId);
        activeRentals = rentalService.getActiveRentalsByMemberId(currentMemberId);
        rentalHistory = new ArrayList<>();
        allCostumes = costumeManager.loadCostumes();
        
        // 履歴（返却済み・キャンセル済み）を取得
        for (Rental rental : allRentals) {
            if (rental.getStatus() == Rental.RentalStatus.RETURNED || 
                rental.getStatus() == Rental.RentalStatus.CANCELLED) {
                rentalHistory.add(rental);
            }
        }
    }
    
    private void initializeComponents() {
        // タブパネル
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // アクティブレンタルテーブル
        String[] activeColumns = {"Costume", "Event", "Size", "Start Date", "Return Date", "Days Left", "Status", "Cost"};
        activeTableModel = new DefaultTableModel(activeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // テーブルを読み取り専用に
            }
        };
        activeRentalsTable = new JTable(activeTableModel);
        setupTable(activeRentalsTable);
        
        // 履歴テーブル
        String[] historyColumns = {"Costume", "Event", "Rental Period", "Returned Date", "Status", "Total Cost"};
        historyTableModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyTableModel);
        setupTable(historyTable);
        
        // 統計情報ラベル
        totalRentalsLabel = new JLabel();
        totalRentalsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        totalCostLabel = new JLabel();
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 12));
        totalCostLabel.setForeground(new Color(0, 128, 0));
        
        overdueCountLabel = new JLabel();
        overdueCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        overdueCountLabel.setForeground(new Color(255, 69, 0));
    }
    
    private void setupTable(JTable table) {
        table.setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // ヘッダーを中央揃えに設定
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        headerRenderer.setBorder(BorderFactory.createRaisedBevelBorder());
        table.getTableHeader().setDefaultRenderer(headerRenderer);
        
        // ヘッダーの表示設定
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        
        // カスタムレンダラーを設定（状況に応じて色分け）
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        // Size列以降（列インデックス2以降）を中央揃えに設定
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setFont(new Font("Arial", Font.BOLD, 12));
        
        if (table == activeRentalsTable) {
            // アクティブレンタルテーブル：Size(2), Start Date(3), Return Date(4), Days Left(5), Status(6), Cost(7)
            for (int i = 2; i < activeTableModel.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CenterAlignedRenderer());
            }
        } else if (table == historyTable) {
            // 履歴テーブル：Rental Period(2), Returned Date(3), Status(4), Total Cost(5)
            for (int i = 2; i < historyTableModel.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(new CenterAlignedRenderer());
            }
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // ヘッダーパネル
        JPanel headerPanel = createHeaderPanel();
        
        // メインパネル（タブ）
        JPanel mainPanel = createMainPanel();
        
        // 統計パネル
        JPanel statsPanel = createStatsPanel();
        
        // ボタンパネル
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("My Rentals - " + currentMemberId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        
        JLabel dateLabel = new JLabel("As of " + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dateLabel.setForeground(Color.GRAY);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(dateLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 20, 10, 10));
        
        // アクティブレンタルタブ
        JScrollPane activeScrollPane = new JScrollPane(activeRentalsTable);
        activeScrollPane.setPreferredSize(new Dimension(600, 300));
        tabbedPane.addTab("Current Rentals (" + activeRentals.size() + ")", activeScrollPane);
        
        // 履歴タブ
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setPreferredSize(new Dimension(600, 300));
        tabbedPane.addTab("Rental History (" + rentalHistory.size() + ")", historyScrollPane);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Summary",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14)));
        panel.setPreferredSize(new Dimension(200, 400));
        panel.setBackground(Color.WHITE);
        
        // 統計情報の追加
        panel.add(Box.createVerticalStrut(15));
        panel.add(createStatItem("Total Rentals:", totalRentalsLabel));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatItem("Total Spent:", totalCostLabel));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createStatItem("Overdue Items:", overdueCountLabel));
        panel.add(Box.createVerticalStrut(20));
        
        // リフレッシュボタン
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        refreshButton.addActionListener(e -> refreshData());
        panel.add(refreshButton);
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createStatItem(String label, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(180, 25));
        panel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 11));
        
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(10, 20, 15, 20));
        
        JButton backButton = new JButton("Back to Main");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(140, 40));
        backButton.addActionListener(e -> dispose());
        
        panel.add(backButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // テーブル選択時の詳細表示
        activeRentalsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = activeRentalsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showRentalDetails(activeRentals.get(selectedRow));
                }
            }
        });
        
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = historyTable.getSelectedRow();
                if (selectedRow >= 0) {
                    showRentalDetails(rentalHistory.get(selectedRow));
                }
            }
        });
    }
    
    private void loadRentalData() {
        // アクティブレンタルテーブルにデータを追加
        activeTableModel.setRowCount(0);
        for (Rental rental : activeRentals) {
            Costume costume = findCostumeById(rental.getCostumeId());
            String costumeName = (costume != null) ? costume.getCostumeName() : "Unknown";
            String event = (costume != null) ? costume.getEventDisplayName() : "Unknown";
            String size = (costume != null) ? costume.getSize() : "Unknown";
            
            long daysLeft = rental.getReturnDate().toEpochDay() - LocalDate.now().toEpochDay();
            String daysLeftStr = daysLeft >= 0 ? daysLeft + " days" : Math.abs(daysLeft) + " days late";
            
            Object[] row = {
                costumeName,
                event,
                size,
                rental.getFormattedRentalDate(),
                rental.getFormattedReturnDate(),
                daysLeftStr,
                rental.getStatus().getDisplayName(),
                "$" + String.format("%.2f", rental.getTotalPayment())
            };
            activeTableModel.addRow(row);
        }
        
        // 履歴テーブルにデータを追加
        historyTableModel.setRowCount(0);
        for (Rental rental : rentalHistory) {
            Costume costume = findCostumeById(rental.getCostumeId());
            String costumeName = (costume != null) ? costume.getCostumeName() : "Unknown";
            String event = (costume != null) ? costume.getEventDisplayName() : "Unknown";
            
            String period = rental.getFormattedRentalDate() + " - " + rental.getFormattedReturnDate();
            
            Object[] row = {
                costumeName,
                event,
                period,
                rental.getFormattedActualReturnDate(),
                rental.getStatus().getDisplayName(),
                "$" + String.format("%.2f", rental.getTotalPayment())
            };
            historyTableModel.addRow(row);
        }
        
        // 統計情報を更新
        updateStatistics();
    }
    
    private void updateStatistics() {
        // 総レンタル数
        totalRentalsLabel.setText(String.valueOf(allRentals.size()));
        
        // 総費用
        double totalCost = allRentals.stream()
                .mapToDouble(Rental::getTotalPayment)
                .sum();
        totalCostLabel.setText("$" + String.format("%.2f", totalCost));
        
        // 延滞件数
        long overdueCount = activeRentals.stream()
                .filter(rental -> rental.getStatus() == Rental.RentalStatus.OVERDUE)
                .count();
        overdueCountLabel.setText(String.valueOf(overdueCount));
        
        // タブのタイトルを更新
        tabbedPane.setTitleAt(0, "Current Rentals (" + activeRentals.size() + ")");
        tabbedPane.setTitleAt(1, "Rental History (" + rentalHistory.size() + ")");
    }
    
    private Costume findCostumeById(String costumeId) {
        return allCostumes.stream()
                .filter(costume -> costume.getCostumeId().equals(costumeId))
                .findFirst()
                .orElse(null);
    }
    
    private void showRentalDetails(Rental rental) {
        Costume costume = findCostumeById(rental.getCostumeId());
        String costumeName = (costume != null) ? costume.getCostumeName() : "Unknown Costume";
        
        String details = String.format(
            "Rental Details:\n\n" +
            "Rental ID: %s\n" +
            "Costume: %s\n" +
            "Event: %s\n" +
            "Size: %s\n" +
            "Rental Period: %s to %s\n" +
            "Actual Return: %s\n" +
            "Status: %s\n" +
            "Basic Cost: $%.2f\n" +
            "Late Fee: $%.2f\n" +
            "Total Payment: $%.2f\n" +
            "Rental Days: %d\n" +
            "Overdue Days: %d",
            rental.getRentalId(),
            costumeName,
            (costume != null) ? costume.getEventDisplayName() : "Unknown",
            (costume != null) ? costume.getSize() : "Unknown",
            rental.getFormattedRentalDate(),
            rental.getFormattedReturnDate(),
            rental.getFormattedActualReturnDate(),
            rental.getStatus().getDisplayName(),
            rental.getTotalCost(),
            rental.getLateFee(),
            rental.getTotalPayment(),
            rental.getRentalDays(),
            rental.getOverdueDays()
        );
        
        JOptionPane.showMessageDialog(this, details, "Rental Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void refreshData() {
        loadData();
        loadRentalData();
        JOptionPane.showMessageDialog(this, "Data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void setupFrame() {
        setTitle("My Rentals - " + currentMemberId);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(800, 500));
    }
    
    /**
     * 中央揃え専用のテーブルセルレンダラー
     */
    private class CenterAlignedRenderer extends DefaultTableCellRenderer {
        public CenterAlignedRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Arial", Font.BOLD, 12));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                
                // 状況に応じて背景色を変更
                if (table == activeRentalsTable && row < activeRentals.size()) {
                    Rental rental = activeRentals.get(row);
                    if (rental.getStatus() == Rental.RentalStatus.OVERDUE) {
                        setBackground(new Color(255, 240, 240)); // 薄い赤
                    } else if (rental.getReturnDate().toEpochDay() - LocalDate.now().toEpochDay() <= 1) {
                        setBackground(new Color(255, 255, 230)); // 薄い黄色（返却期限が近い）
                    }
                }
            }
            
            return this;
        }
    }
    
    /**
     * テーブルセルのカスタムレンダラー（状況に応じて色分け）
     */
    private class CustomTableCellRenderer extends JLabel implements TableCellRenderer {
        public CustomTableCellRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            setFont(new Font("Arial", Font.BOLD, 12));
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
                
                // 状況に応じて背景色を変更
                if (table == activeRentalsTable && row < activeRentals.size()) {
                    Rental rental = activeRentals.get(row);
                    if (rental.getStatus() == Rental.RentalStatus.OVERDUE) {
                        setBackground(new Color(255, 240, 240)); // 薄い赤
                    } else if (rental.getReturnDate().toEpochDay() - LocalDate.now().toEpochDay() <= 1) {
                        setBackground(new Color(255, 255, 230)); // 薄い黄色（返却期限が近い）
                    }
                }
            }
            
            return this;
        }
    }
}