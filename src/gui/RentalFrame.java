package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * レンタル手続き画面
 */
public class RentalFrame extends JFrame {
    private String currentMemberId;
    private Costume selectedCostume;
    private RentalService rentalService;
    
    // UI Components
    private JLabel costumeImageLabel;
    private JLabel costumeNameLabel;
    private JLabel costumeEventLabel;
    private JLabel costumeSizeLabel;
    private JLabel costumeStockLabel;
    private JLabel dailyRateLabel;
    
    private JSpinner rentalDaysSpinner;
    private JSpinner startDateSpinner; // 開始日選択用
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel totalCostLabel;
    private JTextArea termsTextArea;
    private JCheckBox agreeCheckBox;
    
    private JButton confirmButton;
    private JButton cancelButton;
    
    // 設定値
    private static final int MIN_RENTAL_DAYS = 1;
    private static final int MAX_RENTAL_DAYS = 30;
    private static final double DAILY_RATE_MULTIPLIER = 1.0; // 基本料金に対する日額の倍率

    public RentalFrame(String memberId, Costume costume) {
        this.currentMemberId = memberId;
        this.selectedCostume = costume;
        this.rentalService = new RentalService();
        
        initializeComponents();
        setupLayout();
        setupEventListeners();
        updateCostumeInfo();
        updatePriceCalculation();
        setupFrame();
    }
    
    private void initializeComponents() {
        // 衣装情報表示用コンポーネント
        costumeImageLabel = new JLabel();
        costumeImageLabel.setPreferredSize(new Dimension(200, 150));
        costumeImageLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        costumeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        costumeNameLabel = new JLabel();
        costumeNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        
        costumeEventLabel = new JLabel();
        costumeEventLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        costumeSizeLabel = new JLabel();
        costumeSizeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        costumeStockLabel = new JLabel();
        costumeStockLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        dailyRateLabel = new JLabel();
        dailyRateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        dailyRateLabel.setForeground(new Color(0, 128, 0));
        
        // レンタル期間選択
        rentalDaysSpinner = new JSpinner(new SpinnerNumberModel(3, MIN_RENTAL_DAYS, MAX_RENTAL_DAYS, 1));
        rentalDaysSpinner.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        // 開始日選択
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1); // 明日から開始可能
        java.util.Date minDate = calendar.getTime();
        
        calendar.add(java.util.Calendar.MONTH, 3); // 3ヶ月後まで選択可能
        java.util.Date maxDate = calendar.getTime();
        
        startDateSpinner = new JSpinner(new SpinnerDateModel(minDate, minDate, maxDate, java.util.Calendar.DAY_OF_MONTH));
        startDateSpinner.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        
        // 日付フォーマットを設定
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy/MM/dd");
        startDateSpinner.setEditor(dateEditor);
        
        startDateLabel = new JLabel();
        startDateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        endDateLabel = new JLabel();
        endDateLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // 料金表示
        totalCostLabel = new JLabel();
        totalCostLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        totalCostLabel.setForeground(new Color(220, 20, 60));
        
        // 利用規約
        termsTextArea = new JTextArea();
        termsTextArea.setEditable(false);
        termsTextArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        termsTextArea.setBackground(getBackground());
        termsTextArea.setLineWrap(true); // 自動改行を有効化
        termsTextArea.setWrapStyleWord(true); // 単語単位で改行
        termsTextArea.setText(getTermsAndConditions());
        
        // 同意チェックボックス
        agreeCheckBox = new JCheckBox("I agree to the terms and conditions");
        agreeCheckBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        agreeCheckBox.setBackground(Color.WHITE);
        
        // ボタン
        confirmButton = new JButton("Confirm Rental");
        confirmButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(150, 40));
        confirmButton.setEnabled(false); // 最初は無効
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 40));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // ヘッダーパネル
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Costume Rental");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));
        headerPanel.add(titleLabel);
        
        // メインパネル
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // 左パネル（衣装情報）
        JPanel costumePanel = createCostumeInfoPanel();
        
        // 右パネル（レンタル詳細）
        JPanel rentalPanel = createRentalDetailsPanel();
        
        mainPanel.add(costumePanel, BorderLayout.WEST);
        mainPanel.add(rentalPanel, BorderLayout.CENTER);
        
        // ボタンパネル
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createCostumeInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Costume Information",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        panel.setPreferredSize(new Dimension(250, 400));
        
        // 画像パネル
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imagePanel.add(costumeImageLabel);
        
        // 情報パネル
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        infoPanel.add(costumeNameLabel);
        infoPanel.add(costumeEventLabel);
        infoPanel.add(costumeSizeLabel);
        infoPanel.add(costumeStockLabel);
        infoPanel.add(new JSeparator());
        infoPanel.add(dailyRateLabel);
        
        panel.add(imagePanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRentalDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Rental Details",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        
        // 上部：レンタル期間選択
        JPanel periodPanel = createRentalPeriodPanel();
        
        // 中央：利用規約
        JPanel termsPanel = createTermsPanel();
        
        // 下部：料金表示
        JPanel costPanel = createCostPanel();
        
        panel.add(periodPanel, BorderLayout.NORTH);
        panel.add(termsPanel, BorderLayout.CENTER);
        panel.add(costPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createRentalPeriodPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        
        // 会員ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel memberLabel = new JLabel("Member ID:");
        memberLabel.setFont(labelFont);
        panel.add(memberLabel, gbc);
        
        gbc.gridx = 1;
        JLabel memberValueLabel = new JLabel(currentMemberId);
        memberValueLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.add(memberValueLabel, gbc);
        
        // レンタル期間
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel daysLabel = new JLabel("Rental Period:");
        daysLabel.setFont(labelFont);
        panel.add(daysLabel, gbc);
        
        gbc.gridx = 1;
        JPanel daysInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        daysInputPanel.add(rentalDaysSpinner);
        daysInputPanel.add(new JLabel(" days"));
        panel.add(daysInputPanel, gbc);
        
        // 開始日
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(labelFont);
        panel.add(startLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(startDateSpinner, gbc);
        
        // 返却予定日
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel endLabel = new JLabel("Return Date:");
        endLabel.setFont(labelFont);
        panel.add(endLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(endDateLabel, gbc);
        
        return panel;
    }
    
    private JPanel createTermsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Terms and Conditions",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 12)));
        
        JScrollPane scrollPane = new JScrollPane(termsTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 180)); // 高さを大きくする
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // チェックボックスのカスタムパネル
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkPanel.setBackground(Color.WHITE);
        checkPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        // チェックボックスのサイズをカスタマイズ
        agreeCheckBox.setIcon(createCustomCheckBoxIcon(false));
        agreeCheckBox.setSelectedIcon(createCustomCheckBoxIcon(true));
        agreeCheckBox.setFocusPainted(false);
        agreeCheckBox.setBorderPainted(false);
        agreeCheckBox.setContentAreaFilled(false);
        
        checkPanel.add(agreeCheckBox);
        panel.add(checkPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * カスタムチェックボックスアイコンを作成
     */
    private Icon createCustomCheckBoxIcon(boolean checked) {
        int size = 20; // チェックボックスのサイズ
        
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 外枠
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x + 1, y + 1, size - 2, size - 2);
                
                // 背景
                if (checked) {
                    g2d.setColor(new Color(70, 130, 180));
                    g2d.fillRect(x + 2, y + 2, size - 3, size - 3);
                    
                    // チェックマーク
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int[] checkX = {x + 5, x + 8, x + 15};
                    int[] checkY = {y + 10, y + 13, y + 7};
                    for (int i = 0; i < checkX.length - 1; i++) {
                        g2d.drawLine(checkX[i], checkY[i], checkX[i + 1], checkY[i + 1]);
                    }
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(x + 2, y + 2, size - 3, size - 3);
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
        };
    }
    
    private JPanel createCostPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Total Cost",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font(Font.SANS_SERIF, Font.BOLD, 14)));
        
        panel.add(totalCostLabel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(confirmButton);
        
        return panel;
    }
    
    private void setupEventListeners() {
        // レンタル期間変更時の料金更新
        rentalDaysSpinner.addChangeListener(e -> updatePriceCalculation());
        
        // 開始日変更時の料金更新
        startDateSpinner.addChangeListener(e -> updatePriceCalculation());
        
        // 同意チェックボックス
        agreeCheckBox.addActionListener(e -> updateConfirmButtonState());
        
        // 確認ボタン
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRental();
            }
        });
        
        // キャンセルボタン
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void updateCostumeInfo() {
        // 衣装名
        costumeNameLabel.setText("<html><b>" + selectedCostume.getCostumeName() + "</b></html>");
        
        // イベント
        costumeEventLabel.setText("Event: " + selectedCostume.getEventDisplayName());
        
        // サイズ
        costumeSizeLabel.setText("Size: " + selectedCostume.getSize());
        
        // 在庫
        costumeStockLabel.setText("Stock: " + selectedCostume.getStock());
        if (selectedCostume.getStock() <= 2) {
            costumeStockLabel.setForeground(new Color(255, 69, 0));
        } else {
            costumeStockLabel.setForeground(new Color(0, 128, 0));
        }
        
        // 日額料金
        double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
        dailyRateLabel.setText("Daily Rate: $" + String.format("%.2f", dailyRate));
        
        // 画像読み込み
        loadCostumeImage();
    }
    
    private void loadCostumeImage() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/" + selectedCostume.getImagePath()));
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                costumeImageLabel.setIcon(new ImageIcon(img));
                costumeImageLabel.setText("");
            } else {
                costumeImageLabel.setIcon(null);
                costumeImageLabel.setText("No Image");
            }
        } catch (Exception e) {
            costumeImageLabel.setIcon(null);
            costumeImageLabel.setText("No Image");
        }
    }
    
    private void updatePriceCalculation() {
        int days = (Integer) rentalDaysSpinner.getValue();
        
        // 選択された開始日を取得
        java.util.Date selectedDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(selectedDate);
        LocalDate startDate = LocalDate.of(
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        );
        LocalDate endDate = startDate.plusDays(days - 1);
        
        // 日付表示更新
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (E)");
        startDateLabel.setText(startDate.format(formatter));
        endDateLabel.setText(endDate.format(formatter));
        
        // 料金計算
        double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
        double totalCost = dailyRate * days;
        
        totalCostLabel.setText("$" + String.format("%.2f", totalCost) + " (" + days + " days)");
    }
    
    private void updateConfirmButtonState() {
        boolean canConfirm = agreeCheckBox.isSelected() && selectedCostume.getStock() > 0;
        confirmButton.setEnabled(canConfirm);
    }
    
    private void processRental() {
        if (!agreeCheckBox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                "Please agree to the terms and conditions.",
                "Agreement Required",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedCostume.getStock() <= 0) {
            JOptionPane.showMessageDialog(this,
                "This costume is currently out of stock.",
                "Out of Stock",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int days = (Integer) rentalDaysSpinner.getValue();
            
            // 選択された開始日を取得
            java.util.Date selectedDate = (java.util.Date) startDateSpinner.getValue();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(selectedDate);
            LocalDate startDate = LocalDate.of(
                cal.get(java.util.Calendar.YEAR),
                cal.get(java.util.Calendar.MONTH) + 1,
                cal.get(java.util.Calendar.DAY_OF_MONTH)
            );
            LocalDate endDate = startDate.plusDays(days - 1);
            double dailyRate = selectedCostume.getPrice() * DAILY_RATE_MULTIPLIER;
            double totalCost = dailyRate * days;
            
            // レンタル作成
            boolean success = rentalService.createRental(
                currentMemberId,
                selectedCostume.getCostumeId(),
                startDate,
                endDate,
                totalCost
            );
            
            if (success) {
                // 成功メッセージ
                String message = String.format(
                    "Rental confirmed successfully!\n\n" +
                    "Costume: %s\n" +
                    "Period: %s to %s\n" +
                    "Total Cost: $%.2f\n\n" +
                    "Please pick up the costume on the start date.\n" +
                    "Thank you for using our service!",
                    selectedCostume.getCostumeName(),
                    startDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    endDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                    totalCost
                );
                
                JOptionPane.showMessageDialog(this,
                    message,
                    "Rental Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose(); // ウィンドウを閉じる
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to process rental. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void setupFrame() {
        setTitle("Costume Rental - " + selectedCostume.getCostumeName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 700); // 高さを100px増加
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private String getTermsAndConditions() {
        return "RENTAL TERMS AND CONDITIONS\n\n" +
               "1. Rental Period: The costume must be returned by the specified return date.\n\n" +
               "2. Late Returns: A late fee of 10% of the daily rate will be charged for each day the costume is returned late.\n\n" +
               "3. Damage Policy: Any damage to the costume will result in repair charges or full replacement cost.\n\n" +
               "4. Cleaning: Costumes must be returned in clean condition. Additional cleaning fees may apply.\n\n" +
               "5. Cancellation: Rentals can be cancelled up to 24 hours before the start date for a full refund.\n\n" +
               "6. Pick-up: Costumes must be picked up on the rental start date during business hours.\n\n" +
               "7. Liability: The renter is responsible for the costume during the rental period.\n\n" +
               "8. Payment: Full payment is required at the time of rental confirmation.\n\n" +
               "By checking the agreement box, you acknowledge that you have read and agree to these terms.";
    }
}