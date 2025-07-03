package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private String currentMemberId;
    private JLabel welcomeLabel;
    private JLabel timeLabel;
    private JButton searchButton;
    private JButton myRentalsButton;
    private JButton reservationsButton;
    private JButton accountButton;
    private JButton logoutButton;
    private Timer clockTimer;
    
    public MainFrame(String memberId) {
        this.currentMemberId = memberId;
        initializeComponents();
        setupLayout();
        setupEventListeners();
        setupFrame();
        startClock();
    }
    
    private void initializeComponents() {
        // ラベルの初期化
        welcomeLabel = new JLabel("Welcome, " + currentMemberId + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(70, 130, 180));
        
        timeLabel = new JLabel();
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(Color.GRAY);
        updateTimeLabel();
        
        // メインメニューボタン
        searchButton = new JButton("Search Costumes");
        myRentalsButton = new JButton("My Rentals");
        reservationsButton = new JButton("My Reservations");
        accountButton = new JButton("Account Settings");
        logoutButton = new JButton("Logout");
        
        // ボタンのスタイル設定
        setupButtonStyle(searchButton, new Color(70, 130, 180));
        setupButtonStyle(myRentalsButton, new Color(60, 179, 113));
        setupButtonStyle(reservationsButton, new Color(255, 165, 0));
        setupButtonStyle(accountButton, new Color(147, 112, 219));
        setupButtonStyle(logoutButton, new Color(220, 20, 60));
        
        // すべてのボタンのフォーカスを無効化（点滅停止）
        searchButton.setFocusable(false);
        myRentalsButton.setFocusable(false);
        reservationsButton.setFocusable(false);
        accountButton.setFocusable(false);
        logoutButton.setFocusable(false);
        
        // ボタンサイズを統一
        Dimension buttonSize = new Dimension(200, 50);
        searchButton.setPreferredSize(buttonSize);
        myRentalsButton.setPreferredSize(buttonSize);
        reservationsButton.setPreferredSize(buttonSize);
        accountButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);
        
        // ツールチップの設定
        searchButton.setToolTipText("Browse and search available costumes");
        myRentalsButton.setToolTipText("View your current and past rentals");
        reservationsButton.setToolTipText("Manage your costume reservations");
        accountButton.setToolTipText("Update your profile information");
        logoutButton.setToolTipText("Sign out from the system");
    }
    
    private void setupButtonStyle(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // ヘッダーパネル
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // システムタイトル
        JLabel systemTitle = new JLabel("Costume Rental Management System");
        systemTitle.setFont(new Font("Arial", Font.BOLD, 18));
        systemTitle.setForeground(new Color(70, 130, 180));
        
        // ヘッダー情報パネル
        JPanel headerInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerInfoPanel.setBackground(new Color(245, 245, 245));
        headerInfoPanel.add(systemTitle);
        
        // 時刻表示パネル
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timePanel.setBackground(new Color(245, 245, 245));
        timePanel.add(timeLabel);
        
        headerPanel.add(headerInfoPanel, BorderLayout.WEST);
        headerPanel.add(timePanel, BorderLayout.EAST);
        
        // ウェルカムパネル
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        welcomePanel.add(welcomeLabel);
        
        // メインメニューパネル
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 2列レイアウトでボタンを配置
        gbc.gridx = 0; gbc.gridy = 0;
        menuPanel.add(searchButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        menuPanel.add(myRentalsButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        menuPanel.add(reservationsButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        menuPanel.add(accountButton, gbc);
        
        // ログアウトボタンは中央に配置
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 15, 10);
        menuPanel.add(logoutButton, gbc);
        
        // 統計情報パネル（今後の拡張用）
        JPanel statsPanel = createStatsPanel();
        
        // フレームに追加
        add(headerPanel, BorderLayout.NORTH);
        add(welcomePanel, BorderLayout.CENTER);
        add(menuPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // 統計情報ラベル（後で実装）
        JLabel statsLabel = new JLabel("System Status: Online | Active Rentals: -- | Available Costumes: --");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statsLabel.setForeground(Color.GRAY);
        
        statsPanel.add(statsLabel);
        return statsPanel;
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCostumeSearch();
            }
        });
        
        myRentalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMyRentals();
            }
        });
        
        reservationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openReservations();
            }
        });
        
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAccountSettings();
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }
    
    private void setupFrame() {
        setTitle("Costume Rental System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null); // 画面中央に配置
        setResizable(true);
        setMinimumSize(new Dimension(500, 400));
    }
    
    private void startClock() {
        // 1秒ごとに時刻を更新するタイマー
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeLabel();
            }
        });
        clockTimer.start();
    }
    
    private void updateTimeLabel() {
        LocalDateTime now = LocalDateTime.now();
        String timeText = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        timeLabel.setText(timeText);
    }
    
    // 各機能を開くメソッド
    private void openCostumeSearch() {
        try {
            CostumeSearchFrame searchFrame = new CostumeSearchFrame(currentMemberId);
            searchFrame.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Failed to open costume search: " + e.getMessage());
        }
    }
    
    private void openMyRentals() {
        try {
            MyRentalsFrame rentalsFrame = new MyRentalsFrame(currentMemberId);
            rentalsFrame.setVisible(true);
        } catch (Exception e) {
            showErrorMessage("Failed to open rentals: " + e.getMessage());
        }
    }
    
    private void openReservations() {
        try {
            // TODO: ReservationsFrameを実装後に有効化
            // ReservationsFrame reservationsFrame = new ReservationsFrame(currentMemberId);
            // reservationsFrame.setVisible(true);
            
            // 一時的なメッセージ
            JOptionPane.showMessageDialog(this,
                "Reservations feature will be implemented next.",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            showErrorMessage("Failed to open reservations: " + e.getMessage());
        }
    }
    
    private void openAccountSettings() {
        try {
            AccountSettingsFrame accountFrame = new AccountSettingsFrame(currentMemberId);
            accountFrame.setVisible(true);
            this.setVisible(false); // メイン画面を隠す
        } catch (Exception e) {
            showErrorMessage("Failed to open account settings: " + e.getMessage());
        }
    }
    
    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            // タイマーを停止
            if (clockTimer != null) {
                clockTimer.stop();
            }
            
            // ログイン画面に戻る
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginFrame().setVisible(true);
                    MainFrame.this.dispose();
                }
            });
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // 現在のメンバーIDを取得するメソッド
    public String getCurrentMemberId() {
        return currentMemberId;
    }
    
    // ウィンドウが閉じられる際の処理
    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}